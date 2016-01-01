# Game turn timer

A simple single-page webapp for keep tracking of turns during
games and keeping the game moving with a per-turn timelimit.

Designed so that each player opens the app, one person creates a game,
then everyone else joins through scanning a QR code, sharing a
keyword, or other possible methods.

## Development

Open a terminal and type `lein repl` to start a Clojure REPL
(interactive prompt).

In the REPL, type

```clojure
(run)
(browser-repl)
```

The call to `(run)` does two things, it starts the webserver at port
10555, and also the Figwheel server which takes care of live reloading
ClojureScript code and LESS. Give them some time to start.

Running `(browser-repl)` starts the Weasel REPL server, and drops you
into a ClojureScript REPL. Evaluating expressions here will only work
once you've loaded the page, so the browser can connect to Weasel.

When you see the line `Successfully compiled "resources/public/app.js"
in 21.36 seconds.`, you're ready to go. Browse to
`http://localhost:10555` and enjoy.

**Attention: It is not longer needed to run `lein figwheel`
  separately. This is now taken care of behind the scenes**

## Trying it out

If all is well you now have a browser window saying 'Hello Chestnut',
and a REPL prompt that looks like `cljs.user=>`.

Open `resources/public/css/style.css` and change some styling of the
H1 element. Notice how it's updated instantly in the browser.

Open `src/cljs/gameserver/core.cljs`, and change `dom/h1` to
`dom/h2`. As soon as you save the file, your browser is updated.

In the REPL, type

```
(ns gameserver.core)
(swap! app-state assoc :text "Interactivity FTW")
```

Notice again how the browser updates.

## License

Copyright © 2015 George Pittarelli

Distributed under the MIT License.

## Chestnut

Created with [Chestnut](http://plexus.github.io/chestnut/) 0.8.2-SNAPSHOT (1cc67d15).
