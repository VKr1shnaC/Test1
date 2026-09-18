(function(){
  try {
    if (window.__homeflowStableV6) return;
    window.__homeflowStableV6 = true;

    const style = document.createElement('style');
    style.id = 'homeflow-stable-v6-css';
    style.textContent = `
      html,body{width:100%;min-height:100%;overflow-x:hidden;overscroll-behavior:none!important}
      button,.settingEntry,.statusBtn,.editorBtn{-webkit-tap-highlight-color:transparent}
      @media(max-width:600px){
        body{margin:0!important;padding:0!important;min-height:100vh!important;background:linear-gradient(145deg,#fbfcf9,#f5f8f8 52%,#fbf5ef)!important}
        .phone{width:100%!important;max-width:none!important;min-height:100vh!important;margin:0!important;border:0!important;border-radius:0!important;box-shadow:none!important;padding:12px 10px 106px!important}
        .top{padding-top:8px!important}
        .headline{font-size:clamp(23px,7vw,28px)!important;line-height:1.08!important}
        .summary{max-width:58vw!important}
        .nav{position:fixed!important;left:8px!important;right:8px!important;bottom:7px!important;width:auto!important;margin:0!important;z-index:80!important}
        .sheet{padding:0!important;align-items:flex-end!important}
        .modal{width:100%!important;max-width:none!important;max-height:calc(100vh - 14px)!important;border-radius:26px 26px 0 0!important;padding:18px 18px 92px!important;overscroll-behavior:contain!important}
        #modalBody>button.primary.full:last-child,#modalBody>.grid2:last-child{position:sticky!important;bottom:0!important;z-index:25!important;margin-top:12px!important;background:#fffaf5!important;padding-top:8px!important;padding-bottom:8px!important}
      }
    `;
    document.head.appendChild(style);

    function trackerDay(){
      const now = new Date();
      return (now.getFullYear()===2026 && now.getMonth()===8) ? Math.min(30,Math.max(1,now.getDate())) : 16;
    }
    function dateLabel(){
      const d = new Date(2026,8,trackerDay());
      return d.toLocaleDateString('en-GB',{weekday:'short',day:'2-digit',month:'short',year:'numeric'});
    }
    function serviceDue(){
      const full = calcServices();
      let scheduled=0,deduct=0,milkDelivered=0;
      const limit=Math.min(trackerDay(),full.dim);
      for(let d=1; d<=limit; d++){
        const x=getDay(d);
        if(!isSunday(d)){ scheduled++; if(x.maid!=='Came') deduct++; }
        if(x.milk==='Delivered') milkDelivered++;
      }
      const maidPay=(scheduled-deduct)*full.daily;
      const milkPay=milkDelivered*state.settings.milkRate;
      return {full,scheduled,deduct,milkDelivered,maidPay,milkPay,total:maidPay+milkPay};
    }
    function ensureBillsShortcut(){
      const preview=document.getElementById('homeCategoryPreview');
      if(!preview || document.getElementById('hfManageBills')) return;
      const b=document.createElement('button');
      b.id='hfManageBills';
      b.className='ghost full';
      b.style.marginTop='10px';
      b.textContent='🧾 Manage / edit bills';
      b.onclick=function(e){e.stopPropagation();window.goBills();};
      preview.insertAdjacentElement('afterend',b);
    }
    window.goBills=function(){
      if(typeof closeSheet==='function') closeSheet();
      const nav=document.querySelector('.nav button[data-screen="money"]');
      goScreen('money',nav);
      const b=document.querySelector('.subtabs button[data-sub="bills"]');
      if(b) switchMoneySub('bills',b);
      setTimeout(()=>window.scrollTo(0,0),20);
    };
    window.deleteBill=function(id){
      const bill=state.bills.find(x=>x.id===id);
      if(!bill) return;
      if(state.settings.confirmDelete!==false && !confirm('Delete '+bill.name+'?')) return;
      state.bills=state.bills.filter(x=>x.id!==id);
      saveState(); renderBills(); renderHome(); toast('Bill deleted');
    };
    const originalRenderBills=window.renderBills;
    window.renderBills=function(){
      if(originalRenderBills) originalRenderBills();
      document.querySelectorAll('#billsList .card').forEach(card=>{
        if(card.querySelector('.hfDeleteBill')) return;
        const edit=card.querySelector('button[onclick*="editBill"]');
        if(!edit) return;
        const m=(edit.getAttribute('onclick')||'').match(/editBill\('([^']+)'\)/);
        if(!m) return;
        const row=edit.parentElement;
        if(row){row.classList.remove('grid2');row.classList.add('grid3');}
        const del=document.createElement('button');
        del.className='danger hfDeleteBill';
        del.textContent='Delete';
        del.onclick=()=>deleteBill(m[1]);
        row.appendChild(del);
      });
    };

    const originalRenderHome=window.renderHome;
    window.renderHome=function(){
      try { if(originalRenderHome) originalRenderHome(); } catch(e) { console.error(e); }
      try {
        const d=serviceDue();
        const greeting=document.getElementById('homeGreeting');
        if(greeting) greeting.textContent=state.settings.homeName+' • '+dateLabel();
        const total=document.getElementById('serviceTotal');
        if(total) total.textContent=state.settings.showHomeAmounts?money(d.total):'••••';
        const breakdown=document.getElementById('serviceBreakdown');
        if(breakdown) breakdown.innerHTML=
          '<div class="bill"><div class="row"><div><b>🧹 Maid • payable to date</b><div class="tiny">'+
          (d.scheduled-d.deduct)+'/'+d.scheduled+' elapsed scheduled days • '+money(d.full.daily)+'/day</div></div><b>'+money(d.maidPay)+'</b></div></div>'+
          '<div class="bill"><div class="row"><div><b>🥛 Milk • payable to date</b><div class="tiny">'+
          d.milkDelivered+' delivered through '+dateLabel()+'</div></div><b>'+money(d.milkPay)+'</b></div></div>'+
          '<div class="tiny" style="margin-top:8px">Projected month total: <b>'+money(d.full.total)+'</b></div>';
        ensureBillsShortcut();
      } catch(e) { console.error(e); }
    };

    const scan=[...document.querySelectorAll('#import button')].find(b=>b.textContent.includes('Scan phone SMS'));
    if(scan){
      scan.onclick=function(){
        const s=document.getElementById('importStatus');
        if(s) s.innerHTML='<div class="ok">Direct SMS is disabled in Stable v6. Use XML / ZIP backup import here. SMS access will be tested separately.</div>';
      };
    }
    const importSub=document.querySelector('#import>.tiny');
    if(importSub) importSub.textContent='XML / ZIP works fully offline. Direct SMS is kept out of this stable build.';

    document.addEventListener('focusin',function(e){
      if(e.target && e.target.matches && e.target.matches('input,select,textarea')){
        setTimeout(()=>{ try{ e.target.scrollIntoView({block:'center',behavior:'smooth'}); }catch(_){} },180);
      }
    });

    renderBills();
    renderHome();
  } catch (e) {
    console.error('HomeFlow stable patch error',e);
  }
})();