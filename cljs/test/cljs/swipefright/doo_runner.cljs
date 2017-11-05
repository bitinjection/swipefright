(ns swipefright.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [swipefright.core-test]))

(doo-tests 'swipefright.core-test)
