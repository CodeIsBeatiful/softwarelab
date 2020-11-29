<template>
  <div>
    <div id="terminal" ref="terminal" />
  </div>
</template>

<script>

import { Terminal } from 'xterm'
import { FitAddon } from 'xterm-addon-fit'
import { WebLinksAddon } from 'xterm-addon-web-links'
import { getToken } from '@/utils/auth'
import { param2Obj,getAddress } from '@/utils'
// This is particularly important, be sure to introduce css at the beginning, otherwise it may cause style errors
import 'xterm/css/xterm.css'

export default {
  name: 'WebShell',
  data() {
    return {
      socket: '',
      term: null,
      id: param2Obj(window.location.href).id,
      token: getToken()
    }
  },
  mounted() {
    // This is particularly important, be sure to introduce css at the beginning, otherwise it may cause style errors
    this.term = new Terminal({
      cursorBlink: true
    })

    // Load adaptive components
    this.fitAddon = new FitAddon()
    this.term.loadAddon(this.fitAddon)

    // Load weblink components
    this.term.loadAddon(new WebLinksAddon())

    // Initialize the window on the bound component
    this.term.open(this.$refs.terminal)

    // After the window is initialized, it adapts to the size of the browser window
    this.fitAddon.fit()

    // focus
    this.term.focus()

    // Create ws instance
    // Here, the column and row of the window are also passed into the back end,
    // so that it can be automatically changed to output for the front window border

    this.socket = new WebSocket('ws://' + getAddress() + `/api/ws/terminal/${this.id}?token=${this.token}&cols=${this.term.cols}&rows=${this.term.rows}`)
    const that = this
    this.socket.addEventListener('message', function(event) {
      console.info(event.data)
      const message = JSON.parse(event.data)
      const splitLines = message.detail.split('\n')
      if (splitLines.length > 0 && splitLines[splitLines.length - 1].length === 0) {
        // remove last line
        splitLines.pop()
      }
      splitLines.forEach((value) => {
        if (message.type === 'error') {
          that.term.writeln('\x1B[1;3;31m' + value + '\x1B[0m')
        } else {
          that.term.writeln('\x1B[1;3;32m' + value + '\x1B[0m')
        }
      })
      that.term.prompt()
    })

    this.cache = []
    // https://github.com/xtermjs/xterm.js/blob/master/demo/client.ts
    this.term.prompt = () => {
      this.term.write('\r\n$ ')
    }

    this.term.writeln('Welcome to Software Lab')
    this.term.writeln('Software Lab allows you to quickly get an application with one click')
    this.term.writeln('')
    this.term.prompt()
    this.term.onKey((e) => {
      const ev = e.domEvent
      const printable = !ev.altKey && !ev.ctrlKey && !ev.metaKey

      if (ev.keyCode === 13) { // Enter
        // this.term.prompt()
        this.socket.send(this.cache.join(''))
        this.term.writeln('')
        this.cache = []
      } else if (ev.keyCode === 8) { // Backspace
        // Do not delete the prompt
        this.cache.pop()
        if (this.term._core.buffer.x > 2) {
          this.term.write('\b \b')
        }
      } else if (printable) {
        this.term.write(e.key)
        this.cache.push(e.key)
      }
    })

    // Listen to resize, when the window is dragged, listen to events and change the xterm window in real time
    window.addEventListener('resize', this.debounce(this.resizeScreen, 1500), false)
  },
  methods: {
    // Throttling to avoid frequent requests for updates to the backend when dragging
    debounce(fn, wait) {
      let timeout = null
      return function() {
        if (timeout !== null) clearTimeout(timeout)
        timeout = setTimeout(fn, wait)
      }
    },
    // When the page is resized, you need to tell the backend cols and rows again
    resizeScreen() {
      // Refit the window of xterm to get new cols and rows
      this.fitAddon.fit()
      this.socket.send(JSON.stringify([this.term.cols, this.term.rows]))
    }
  }

}
</script>
