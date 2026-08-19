const fs = require('fs');
const OUT = 'C:/dev/clearStreak/app/src/main/assets/proverbs_web.txt';
(async () => {
  const header = [
    '# ClearStreak Heritage Vault — Proverbs (World English Bible, public domain)',
    '# Source: bible-api.com (WEB translation). Format: chapter|verse|text',
    ''
  ];
  const lines = [];
  for (let ch = 1; ch <= 31; ch++) {
    let ok = false;
    for (let attempt = 0; attempt < 4 && !ok; attempt++) {
      try {
        const res = await fetch(`https://bible-api.com/proverbs+${ch}?translation=web`);
        if (!res.ok) throw new Error('status ' + res.status);
        const data = await res.json();
        for (const v of data.verses) {
          const text = String(v.text).replace(/\s+/g, ' ').replace(/\|/g, '/').trim();
          lines.push(`${v.chapter}|${v.verse}|${text}`);
        }
        ok = true;
      } catch (e) {
        await new Promise(r => setTimeout(r, 600));
      }
    }
    if (!ok) { console.error('FAILED chapter', ch); process.exit(1); }
    await new Promise(r => setTimeout(r, 120));
  }
  fs.writeFileSync(OUT, header.concat(lines).join('\n') + '\n');
  console.log('Wrote ' + lines.length + ' verses across 31 chapters');
})();
