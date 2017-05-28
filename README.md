## Welcome to bengine!

[Demo](http://escherize.com/bengine/public)

The data oriented static site generator.

## Why bengine?

This could be the most _simple_ blog framework you can find.

Static site generators (take some meaty source material (usually text) in, and spit out a carefully crafted website.

With bengine, you will write easy, simple, and powerful tags for your thoughts to turn them into a killer blog.

No waiting around with bengine -- It's running on clojurescript on node, so re-renders are _fast_.

### Origin story

I really wanted to use Matthew Butterick's amazing [pollen](http://docs.racket-lang.org/pollen/) to build a blog for myself. But I found nesting radcket's x-expressions too unweidly, and didn't want to learn how to make it work.  Based on the advice of a friend, I decided to see if I could clone pollen's key ideas into a clojurescript-on-node based site generator.

### Philosophy

There are not a lot of moving parts here, folks.

1. Posts

Posts look like the [hiccup](http://hiccup.space) style of html. Hiccup is not verbose and stays out of your way so you can think clearly.

Example:
``` clojure
[:article
 [:h1 "Welcome to Fight Club. ^_^"]
 "I hope you have a fantastic time! :)"
 "Glad you could join us today."]
```

2. Tags

Posts can contain tags (a few are included). You are free to create any tag function you want in `_tags/tags.cljs`, and use it in your posts.

#### Example 1

In `_tags/tags.cljs`:
``` clojure
(defn heading [s] [:h1 s])
```

In one of your posts in `_posts/any_post_name.edn`:
``` clojure
(my/heading "Hello")
```

Compile your blog, and see:
``` clojure
"<h1>Hello</h1>"
```

#### Example 2

In your tags:
```clojure
(defn rules [& rs]
       (into [:ol] (map #(vector :li %) rs)))
```

Then, in your post:
```clojure
[:article
 [:h1 "Hey friend!"]
 (rules
  "The first rule of Fight Club is: you do not talk about Fight Club."
  "The second rule of Fight Club is: you DO NOT talk about Fight Club!"
  "Third rule of Fight Club: if someone yells “stop!”, goes limp, or taps out, the fight is over."
  "Fourth rule: only two guys to a fight. "
  "Fifth rule: one fight at a time, fellas."
  "Sixth rule: the fights are bare knuckle. No shirt, no shoes, no weapons."
  "Seventh rule: fights will go on as long as they have to."
  "And the eighth and final rule: if this is your first time at Fight Club, you have to fight.")]
```

3. Templates (optional)

There are 2 templates that you can use to customize how *posts* and your *index* page look. They're in `_tags/tags.cljs` and called `post-template` and `index-template` respectively, and can be edited like any other tag.

    Protip: don't remove those functions.

### Prequisites

[Node.js](https://nodejs.org/en/) needs to be installed to run the application.

### running in development mode

run the following command in the terminal to install NPM modules and start Figwheel:

```
lein build
```

run `node` in another terminal:

```
npm start
```

#### configuring the REPL

Once Figwheel and node are running, you can connect to the remote REPL at `localhost:7000`.

Type `(cljs)` in the REPL to connect to Figwheel ClojureScript REPL.


### building the release version

```
lein package
```

Run the release version:

```
npm start
```
