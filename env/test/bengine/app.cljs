(ns bengine.app
  (:require
    [doo.runner :refer-macros [doo-tests]]
    [bengine.core-test]))

(doo-tests 'bengine.core-test)


