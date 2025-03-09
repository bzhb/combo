(ns combo.test-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [combo.core-test]))

(doo-tests 'combo.core-test)
