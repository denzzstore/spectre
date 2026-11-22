const { channel, projectDir } = require('nodejs-mobile')
const path = require('path')
const P = require('pino')
const {
  default: makeWASocket,
  useMultiFileAuthState,
  Browsers,
  delay,
  DisconnectReason
} = require('@whiskeysockets/baileys')

const AUTH = path.join(projectDir || __dirname, 'auth')
let sock = null

const out = (type, data) => channel.post(JSON.stringify({ type, ...data }))
const log = (line) => out('log', { line })

async function boot() {
  const { state, saveCreds } = await useMultiFileAuthState(AUTH)

  sock = makeWASocket({
    auth: state,
    logger: P({ level: 'silent' }),
    browser: Browsers.ubuntu('Chrome'),
    printQRInTerminal: false,
    syncFullHistory: false,
    markOnlineOnConnect: true,
    generateHighQualityLinkPreview: false,
    getMessage: async () => ({ conversation: 'SPECTRE' })
  })

  sock.ev.on('creds.update', saveCreds)

  sock.ev.on('connection.update', async (u) => {
    if (u.connection === 'open') out('status', { state: 'connected' })
    if (u.connection === 'close') {
      const code = u.lastDisconnect?.error?.output?.statusCode
      out('status', { state: code === DisconnectReason.loggedOut ? 'logged_out' : 'reconnecting' })
      if (code !== DisconnectReason.loggedOut) {
        await delay(1500)
        boot()
      }
    }
  })
}

channel.on('pair', async (raw) => {
  try {
    const phone = String(raw).replace(/[^0-9]/g, '')
    if (!sock) await boot()
    const code = await sock.requestPairingCode(phone)
    out('code', { code })
    log('Kode pairing dibuat untuk ' + phone)
  } catch (e) {
    log('pair error: ' + e.message)
  }
})

channel.on('send', async (raw) => {
  try {
    const { jid, text, mentions } = JSON.parse(raw)
    const msg = { text }
    if (mentions) msg.mentions = mentions.split(',')
    await sock.sendMessage(jid, msg)
  } catch (e) {
    log('send error: ' + e.message)
  }
})

channel.on('group', async (raw) => {
  try {
    const { jid, field, value } = JSON.parse(raw)
    if (field === 'subject') await sock.groupUpdateSubject(jid, value)
    else await sock.groupUpdateDescription(jid, value)
  } catch (e) {
    log('group error: ' + e.message)
  }
})

channel.on('poll', async (raw) => {
  try {
    const { jid, name, options } = JSON.parse(raw)
    await sock.sendMessage(jid, {
      poll: { name, values: options.split('|'), selectableCount: 1 }
    })
  } catch (e) {
    log('poll error: ' + e.message)
  }
})

boot().catch((e) => log('boot fatal: ' + e.message))
