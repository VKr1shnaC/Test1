(function(){
  'use strict';
  if (window.__homeflowV12Enhancements) return;
  window.__homeflowV12Enhancements = true;

  var style = document.createElement('style');
  style.id = 'homeflow-v12-layout';
  style.textContent = [
    'html,body{width:100%!important;height:100%!important;min-height:100%!important;overflow:hidden!important;overscroll-behavior:none!important;background:var(--bg)!important}',
    'body{padding:0!important;position:relative!important}',
    '.phone{width:min(100%,40rem)!important;max-width:40rem!important;height:100dvh!important;min-height:100dvh!important;margin:0 auto!important;overflow-x:hidden!important;overflow-y:auto!important;-webkit-overflow-scrolling:touch!important;overscroll-behavior-y:contain!important;touch-action:pan-y!important;padding:max(12px,env(safe-area-inset-top)) max(12px,env(safe-area-inset-right)) calc(88px + env(safe-area-inset-bottom)) max(12px,env(safe-area-inset-left))!important;scrollbar-width:none!important}',
    '.phone::-webkit-scrollbar{display:none!important}',
    '.screen.active{display:block!important;position:relative!important;min-height:0!important;height:auto!important;overflow:visible!important;touch-action:pan-y!important}',
    '.nav{position:fixed!important;left:50%!important;transform:translateX(-50%)!important;bottom:max(8px,calc(env(safe-area-inset-bottom) + 4px))!important;width:min(calc(100% - 16px),39rem)!important;z-index:100!important}',
    '#home{padding-bottom:2px!important}',
    '.homeExpenseCard{margin-top:12px;padding:14px 15px;border-radius:24px;background:linear-gradient(135deg,#103f3b,#0f6e66);color:#f7f1e6;box-shadow:0 10px 24px rgba(14,74,69,.14);cursor:pointer}',
    '.homeExpenseCard .eyebrow{font-size:10px;opacity:.74;font-weight:800;letter-spacing:.3px}',
    '.homeExpenseCard .sheetTitle{font-family:Fraunces,Georgia,serif;font-size:19px;font-weight:800;margin-top:2px}',
    '.homeExpenseCard .sheetGrid{display:grid;grid-template-columns:1.35fr .9fr .9fr;gap:8px;margin-top:12px}',
    '.homeExpenseCard .sheetMetric{background:rgba(255,255,255,.08);border-radius:15px;padding:10px;min-width:0}',
    '.homeExpenseCard .sheetMetric small{display:block;font-size:9px;opacity:.72;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}',
    '.homeExpenseCard .sheetMetric b{display:block;font-size:15px;margin-top:3px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}',
    '.homeExpenseCard .sheetHint{font-size:10px;opacity:.72;margin-top:10px;display:flex;justify-content:space-between;gap:10px}',
    '.expenseSheetGrid{display:grid;grid-template-columns:1fr 1fr;gap:8px;margin-top:10px}',
    '.expenseSheetMetric{background:var(--elev,#fff);border-radius:16px;padding:12px}',
    '@media(max-width:360px){.homeExpenseCard .sheetGrid{grid-template-columns:1fr 1fr}.homeExpenseCard .sheetMetric:first-child{grid-column:1/-1}}'
  ].join('');
  document.head.appendChild(style);

  function ensureHomeExpenseCard(){
    if (document.getElementById('homeExpenseSheetCard')) return;
    var hero = document.querySelector('#home .hero');
    if (!hero) return;
    var card = document.createElement('div');
    card.id = 'homeExpenseSheetCard';
    card.className = 'homeExpenseCard';
    card.setAttribute('role','button');
    card.setAttribute('tabindex','0');
    card.addEventListener('click', function(){ window.openHomeExpenseSheet(); });
    card.addEventListener('keydown', function(e){
      if (e.key === 'Enter' || e.key === ' ') {
        e.preventDefault();
        window.openHomeExpenseSheet();
      }
    });
    hero.insertAdjacentElement('afterend', card);
  }

  window.renderHomeExpenseSheetCard = function(){
    ensureHomeExpenseCard();
    var card = document.getElementById('homeExpenseSheetCard');
    if (!card) return;
    var spend = Number(householdSpend() || 0);
    var rec = Number(recoverable() || 0);
    var pending = state.bills.filter(function(b){ return monthKey(b.due) === ACTIVE_MONTH && !b.paid; });
    var next = pending.slice().sort(function(a,b){ return String(a.due).localeCompare(String(b.due)); })[0];
    var name = state.settings.homeName || 'My Home';
    card.innerHTML =
      '<div class="eyebrow">SEPTEMBER EXPENSE SHEET</div>' +
      '<div class="sheetTitle">' + esc(name) + ' · Expense sheet</div>' +
      '<div class="sheetGrid">' +
        '<div class="sheetMetric"><small>Household spend</small><b>' + money(spend) + '</b></div>' +
        '<div class="sheetMetric"><small>Recoverable</small><b>' + money(rec) + '</b></div>' +
        '<div class="sheetMetric"><small>Pending bills</small><b>' + pending.length + '</b></div>' +
      '</div>' +
      '<div class="sheetHint"><span>' + (next ? 'Next: ' + esc(next.name) + ' · ' + money(next.amount) : 'No pending bill') + '</span><span>Open sheet ›</span></div>';
  };

  window.openMoneyExpenses = function(){
    closeSheet();
    goScreen('money');
    var btn = document.querySelector('.subtabs button[data-sub="expenses"]');
    if (btn) switchMoneySub('expenses', btn);
    var phone = document.querySelector('.phone');
    if (phone) phone.scrollTop = 0;
  };

  window.openHomeExpenseSheet = function(){
    var spend = Number(householdSpend() || 0);
    var rec = Number(recoverable() || 0);
    var cats = calcCategories();
    var s = calcServices();
    var bills = state.bills.filter(function(b){ return monthKey(b.due) === ACTIVE_MONTH; });
    var pending = bills.filter(function(b){ return !b.paid; });
    var paid = bills.filter(function(b){ return b.paid; });
    var latest = householdTxns().slice().sort(function(a,b){ return String(b.date).localeCompare(String(a.date)); }).slice(0,5);
    var topCats = Object.entries(cats).filter(function(x){ return Number(x[1]) > 0; }).sort(function(a,b){ return b[1]-a[1]; }).slice(0,4);
    var name = state.settings.homeName || 'My Home';

    var topHtml = topCats.length ? topCats.map(function(x){
      return '<div class="bill"><div class="row"><span>' + esc(x[0]) + '</span><b>' + money(x[1]) + '</b></div></div>';
    }).join('') : '<div class="ok">No household spend recorded yet.</div>';

    var latestHtml = latest.length ? latest.map(function(t){
      return '<div class="bill"><div class="row"><div><b>' + esc(t.merchant || t.category || 'Activity') + '</b><div class="tiny">' + esc(t.date) + ' • ' + esc(t.category || t.type) + '</div></div><b>' + money(t.amount) + '</b></div></div>';
    }).join('') : '<div class="ok">No transactions yet.</div>';

    openSheet(
      name + ' · Expense sheet',
      'September 2026 • quick household money snapshot',
      '<div class="expenseSheetGrid">' +
        '<div class="expenseSheetMetric"><div class="tiny">True household spend</div><b>' + money(spend) + '</b></div>' +
        '<div class="expenseSheetMetric"><div class="tiny">Recoverable</div><b>' + money(rec) + '</b></div>' +
        '<div class="expenseSheetMetric"><div class="tiny">Services payable</div><b>' + money(s.total) + '</b></div>' +
        '<div class="expenseSheetMetric"><div class="tiny">Bills</div><b>' + paid.length + ' paid · ' + pending.length + ' pending</b></div>' +
      '</div>' +
      '<div class="panel" style="margin-top:10px"><div class="sec">Top spending areas</div>' + topHtml + '</div>' +
      '<div class="panel" style="margin-top:10px"><div class="sec">Latest activity</div>' + latestHtml + '</div>' +
      '<div class="grid2" style="margin-top:12px">' +
        '<button class="ghost" onclick="openMoneyExpenses()">View expenses</button>' +
        '<button class="primary" onclick="closeSheet();goScreen(\'review\');var p=document.querySelector(\'.phone\');if(p)p.scrollTop=0">Review month</button>' +
      '</div>' +
      '<button class="soft full" style="margin-top:8px" onclick="closeSheet();openTxnModal(\'EXPENSE\',\'Other\')">＋ Add expense</button>'
    );
  };

  var oldRenderHome = window.renderHome;
  window.renderHome = function(){
    if (oldRenderHome) oldRenderHome();
    window.renderHomeExpenseSheetCard();
  };

  var oldGoScreen = window.goScreen;
  window.goScreen = function(id, btn){
    oldGoScreen(id, btn);
    var phone = document.querySelector('.phone');
    if (phone) phone.scrollTop = 0;
  };

  ensureHomeExpenseCard();
  try { window.renderHomeExpenseSheetCard(); } catch(e) { console.error('HomeFlow v12 expense sheet', e); }
})();