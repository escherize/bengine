(ns bengine.specs
  (:require
   [clojure.test.check.generators]
   [clojure.spec.alpha :as s]
   [clojure.string :as str]))

(s/def :post/title string?)
(s/def :post/next-url (s/and
                        string?
                        #(str/starts-with? % "/")
                        #(str/ends-with? % ".html")))
(s/def :post/prev-url (s/and
                        string?
                        #(str/starts-with? % "/")
                        #(str/ends-with? % ".html")))
(s/def :post/my-url (s/and
                      string?
                      #(str/starts-with? % "/")
                      #(str/ends-with? % ".html")))

(s/def :post/creation-time inst?)


(s/def ::post
  (s/keys :req [:post/title
                :post/creation-time
                :post/here
                :post/next
                :post/prev]))
