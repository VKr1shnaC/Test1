(function(){
  if (window.__homeflowV11Enhancements) return;
  window.__homeflowV11Enhancements = true;

  const style = document.createElement('style');
  style.id = 'homeflow-v11-scroll-and-people';
  style.textContent = `
    html{
      width:100%!important;
      min-height:100%!important;
      height:auto!important;
      overflow-x:hidden!important;
      overflow-y:auto!important;
      overscroll-behavior-y:auto!important;
    }
    body{
      width:100%!important;
      min-height:100dvh!important;
      height:auto!important;
      overflow-x:hidden!important;
      overflow-y:auto!important;
      overscroll-behavior-y:auto!important;
      touch-action:auto!important;
      -webkit-overflow-scrolling:touch!important;
      position:relative!important;
    }
    .phone{
      width:min(100%,40rem)!important;
      max-width:40rem!important;
      min-height:100dvh!important;
      height:auto!important;
      overflow:visible!important;
      touch-action:auto!important;
    }
    .screen.active{
      display:block!important;
      height:auto!important;
      min-height:0!important;
      overflow:visible!important;
      touch-action:pan-y!important;
    }
    .sheet{
      overscroll-behavior:contain!important;
      touch-action:auto!important;
      padding:
        max(10px,env(safe-area-inset-top))
        max(10px,env(safe-area-inset-right))
        max(10px,env(safe-area-inset-bottom))
        max(10px,env(safe-area-inset-left))!important;
    }
    .modal{
      width:min(100%,40rem)!important;
      max-height:calc(100dvh - max(20px,env(safe-area-inset-top)) - max(20px,env(safe-area-inset-bottom)))!important;
      overflow-x:hidden!important;
      overflow-y:auto!important;
      overscroll-behavior:contain!important;
      -webkit-overflow-scrolling:touch!important;
      touch-action:pan-y!important;
      padding-bottom:calc(22px + env(safe-area-inset-bottom))!important;
    }
    .personStatus{display:flex;align-items:center;gap:8px;flex-wrap:wrap}
    .personSummary{margin-bottom:2px!important}
    .settleChoice{padding:12px;border-radius:16px;background:var(--elev,#fff);margin-top:10px}
    .settleChoice b{display:block;margin-bottom:4px}
  `;
  document.head.appendChild(style);

  // Ensure no old runtime style can keep the document locked.
  document.documentElement.style.overflowY = 'auto';
  document.body.style.overflowY = 'auto';
  document.body.style.height = 'auto';

  const oldOpenSheet = window.openSheet;
  window.openSheet = function(title, sub, body){
    oldOpenSheet(title, sub, body);
    const modal = document.querySelector('#sheet .modal');
    if (modal) {
      modal.scrollTop = 0;
      modal.style.overflowY = 'auto';
    }
  };

  window.performBillDelete = function(id){
    const bill = state.bills.find(x => x.id === id);
    if (!bill) return;
    state.bills = state.bills.filter(x => x.id !== id);
    closeSheet();
    saveState();
    toast('Bill deleted');
  };

  window.deleteBill = function(id){
    const bill = state.bills.find(x => x.id === id);
    if (!bill) return;
    if (state.settings.confirmDelete === false) {
      performBillDelete(id);
      return;
    }
    openSheet(
      'Delete bill',
      'This removes the bill from HomeFlow.',
      `<div class="panel">
         <div class="tiny">Bill</div>
         <div class="sec" style="margin-top:3px">${esc(bill.name)}</div>
         <div class="amount" style="margin-top:8px">${money(bill.amount)}</div>
         <div class="tiny" style="margin-top:4px">${bill.recurring ? 'Recurring monthly • future recurrence will stop' : 'One-time bill'}</div>
       </div>
       <div class="grid2" style="margin-top:12px">
         <button class="ghost" onclick="closeSheet()">Keep bill</button>
         <button class="danger" onclick="performBillDelete('${bill.id}')">Delete bill</button>
       </div>`
    );
  };

  window.calcPeople = function(){
    const balances = {};
    state.people.forEach(p => balances[p.id] = 0);
    householdTxns().forEach(t => {
      if (t.type === 'ADVANCE' && t.person) {
        balances[t.person] = (balances[t.person] || 0) + Number(t.amount || 0);
      }
      if ((t.type === 'REIMBURSEMENT' || t.type === 'DEBT_CLEARED') && t.person) {
        balances[t.person] = (balances[t.person] || 0) - Number(t.amount || 0);
      }
    });
    return balances;
  };

  window.renderPeople = function(){
    const bal = calcPeople();
    const outstanding = state.people.reduce((sum,p)=>sum + Math.max(0, Number(bal[p.id] || 0)),0);
    const settledCount = state.people.filter(p => Math.max(0, Number(bal[p.id] || 0)) < 0.01).length;
    const summary = `
      <div class="card personSummary">
        <div class="row">
          <div><div class="tiny">Outstanding with people</div><div class="amount">${money(outstanding)}</div></div>
          <div style="text-align:right"><div class="tiny">Settled / clear</div><b>${settledCount} of ${state.people.length}</b></div>
        </div>
      </div>`;
    const cards = state.people.map(p => {
      const due = Math.max(0, Number(bal[p.id] || 0));
      const settled = due < 0.01;
      return `<div class="card" onclick="showPerson('${p.id}')">
        <div class="row">
          <div><b>👤 ${esc(p.name)}</b><div class="recover">${settled ? 'Tap for settled ledger' : 'Tap for ledger & settlement'}</div></div>
          <div class="personStatus">${settled
            ? '<span class="badge green">✓ Settled</span>'
            : `<b>${money(due)} due</b>`}
          </div>
        </div>
      </div>`;
    }).join('');
    document.getElementById('peopleList').innerHTML = summary + cards;
  };

  window.showPerson = function(pid){
    const p = state.people.find(x => x.id === pid);
    if (!p) return;
    const rows = householdTxns().filter(t => t.person === pid).sort((a,b)=>b.date.localeCompare(a.date));
    const bal = Math.max(0, Number(calcPeople()[pid] || 0));
    const settled = bal < 0.01;
    const ledger = rows.map(t => {
      const reduces = t.type === 'REIMBURSEMENT' || t.type === 'DEBT_CLEARED';
      const label = t.type === 'DEBT_CLEARED' ? 'CLEARED / WAIVED' : t.type;
      return `<div class="bill"><div class="row">
        <div><b>${t.date} • ${esc(t.merchant || 'Activity')}</b><div class="${reduces ? 'paid' : 'recover'}">${label}</div></div>
        <b>${reduces ? '-' : '+'}${money(t.amount)}</b>
      </div></div>`;
    }).join('') || '<div class="ok">No reimbursement activity yet.</div>';

    const actions = settled
      ? `<div class="ok" style="margin-top:10px">✓ Balance settled / cleared</div>
         <button class="ghost full" style="margin-top:10px" onclick="openTxnModal('ADVANCE','Other','','${pid}')">＋ Add new advance</button>`
      : `<div class="grid2" style="margin-top:10px">
           <button class="primary" onclick="openTxnModal('REIMBURSEMENT','Settlement','','${pid}')">Record repayment</button>
           <button class="ghost" onclick="openPersonSettlement('${pid}')">Settle / clear</button>
         </div>`;

    openSheet(
      p.name,
      settled ? 'Reimbursement ledger • settled' : 'Reimbursement ledger • open balance',
      `<div class="panel">
         <div class="tiny">${settled ? 'Balance status' : 'Still owed to you'}</div>
         <div class="amount">${settled ? 'Settled' : money(bal)}</div>
         ${!settled ? '<div class="tiny" style="margin-top:4px">Record an actual repayment, or clear/waive the remaining balance.</div>' : ''}
       </div>
       ${ledger}
       ${actions}`
    );
  };

  window.openPersonSettlement = function(pid){
    const p = state.people.find(x => x.id === pid);
    const bal = Math.max(0, Number(calcPeople()[pid] || 0));
    if (!p || bal < 0.01) { toast('This balance is already settled'); return; }
    openSheet(
      'Settle / clear balance',
      `${p.name} • ${money(bal)} outstanding`,
      `<div class="settleChoice">
         <b>How should HomeFlow close this balance?</b>
         <div class="tiny">Paid back records a reimbursement. Clear / waive closes the receivable without counting it as income or household spending.</div>
       </div>
       <select id="settleMode" class="input">
         <option value="paid">Paid back in full</option>
         <option value="cleared">Clear / waive remaining balance</option>
       </select>
       <input id="settleDate" class="input" type="date" value="2026-09-16">
       <select id="settlePayment" class="input">${paymentOptions('UPI')}</select>
       <button class="primary full" style="margin-top:12px" onclick="completePersonSettlement('${pid}')">Confirm settlement</button>
       <button class="ghost full" style="margin-top:8px" onclick="showPerson('${pid}')">Back to ledger</button>`
    );
  };

  window.completePersonSettlement = function(pid){
    const p = state.people.find(x => x.id === pid);
    const amount = Math.max(0, Number(calcPeople()[pid] || 0));
    if (!p || amount < 0.01) { closeSheet(); toast('Already settled'); return; }
    const mode = val('settleMode') || 'paid';
    const paid = mode === 'paid';
    state.txns.push({
      id:id('t'),
      date:val('settleDate') || '2026-09-16',
      amount,
      type: paid ? 'REIMBURSEMENT' : 'DEBT_CLEARED',
      category:'Settlement',
      merchant: paid ? 'Balance settled in full' : 'Balance cleared / waived',
      payment: paid ? (val('settlePayment') || 'UPI') : 'N/A',
      person:pid,
      vehicle:null,
      odo:null,
      forWhom:pid
    });
    closeSheet();
    saveState();
    toast(paid ? 'Debt settled ✓' : 'Balance cleared ✓');
  };

  const oldRenderExpenses = window.renderExpenses;
  window.renderExpenses = function(){
    const rows = householdTxns().slice().sort((a,b)=>b.date.localeCompare(a.date));
    const el = document.getElementById('expenseList');
    if (!el) return oldRenderExpenses && oldRenderExpenses();
    el.innerHTML = rows.map(t => {
      const reducing = ['REIMBURSEMENT','REFUND','DEBT_CLEARED'].includes(t.type);
      return `<div class="card"><div class="row"><div><b>${esc(t.merchant||t.category)}</b><div class="tiny">${t.date} • ${t.type} • ${t.category}${t.person?' • '+personName(t.person):''}</div></div><div style="text-align:right"><b>${reducing?'-':''}${money(t.amount)}</b><div class="tiny">${t.payment==='N/A'?'No payment movement':paymentName(t.payment)}</div></div></div></div>`;
    }).join('');
  };

  // Explain the new balance-clear activity in the People tab.
  const typeCard = [...document.querySelectorAll('#people .card')].find(x => x.querySelector('.sec')?.textContent.includes('Money activity types'));
  if (typeCard && !typeCard.querySelector('.hf-clear-type')) {
    const row = document.createElement('div');
    row.className = 'bill hf-clear-type';
    row.innerHTML = '<div class="row"><div>✓ Balance settled / cleared<div class="tiny">Closes a receivable without creating household income</div></div><span class="badge green">Settlement</span></div>';
    typeCard.appendChild(row);
  }

  // Re-render with the enhanced people states and fixed bill actions.
  try { renderAll(); } catch (e) { console.error('HomeFlow v11 enhancement render', e); }
})();