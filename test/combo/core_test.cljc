(ns combo.core-test
  (:require #?(:clj  [clojure.test :refer [deftest is testing]]
               :cljs [cljs.test :refer-macros [deftest is testing]])
            [combo.core :as c]))

(deftest assoc-when-test
  (testing "assoc-when"
    (is (= {:a 1 :b 2}
           (c/assoc-when {:a 1}
                         true :b 2))
        "Simple nominal case")
    (is (= {:a 1 :b nil}
           (c/assoc-when {:a 1}
                         true :b nil))
        "with a nil key")
    (is (= {:a 1 :b 2 :d nil}
           (c/assoc-when {:a 1}
                         true :b 2
                         false :c 4
                         true :d nil))
        "With a false and a nil key")
    (is (= {:a 1}
           (c/assoc-when nil
                         true :a 1))
        "With a nil map")
    (is (= {:b 2}
           (c/assoc-when nil
                         false :a 1
                         true :b 2))
        "With a nil map and both true/false conditions")
    (is (nil? (c/assoc-when nil
                            false :a 0))
        "Nil result")
    (is (= {:b nil}
           (c/assoc-when nil
                         false :a 0
                         true :b nil))
        "Nil map")
    (let [input (with-meta {:a 1} {:m 42})]
      (is (= {:m 42} (meta (c/assoc-when input
                                         true :b 2
                                         true :c nil
                                         false :d 3)))
          "Metadata conservation"))
    (is (= {:a 1 :b 2 :d nil}
           (-> {:a 1}
               (c/assoc-when
                true :b 2
                false :c 4
                true :d nil)))
        "Inside a threading macro")
    (is (= {:a 0 :b -2 :d 4}
           (c/assoc-when {:a -1 :b -2}
                         true :a 0
                         false :b 1
                         nil :c 2
                         true :d 4))
        "Complex case")))

(deftest assoc-in-when-test
  (testing "assoc-in-when"
    (is (= {:a {:b 1}, :b {:d 4}}
           (c/assoc-in-when {:a {} :b {:d 4}}
                            true [:a :b] 1
                            false [:b :d] 2))
        "Nominal case")))

(deftest dissoc-when-test
  (testing "dissoc-when"
    (is (= {:b -2, :d 4}
           (c/dissoc-when {:a 0 :b -2 :d 4}
                          true :a
                          false :b
                          true :c
                          nil :d))
        "Nominal case")))

(deftest dissoc-in-when-test
  (testing "dissoc-in-when"
    (is (= {:a {:e 5}, :b {:d 4}}
           (c/dissoc-in-when {:a {:c 3 :e 5} :b {:d 4}}
                             true [:a :c]
                             false [:b :d]))
        "Nominal case")))

(deftest update-when-test
  (testing "update-when"
    (is (= {:a 1, :b -2, :d 4, :c 1}
           (c/update-when {:a 0 :b -2 :d 4}
                          true :a inc
                          false :b inc
                          true :c #(if % inc 1)))
        "Nominal case")))

(deftest update-in-when-test
  (testing "update-in-when"
    (is (= {:a {:c 4, :e 5}, :b {:d 4, :c 1}}
           (c/update-in-when {:a {:c 3 :e 5} :b {:d 4}}
                             true [:a :c] inc
                             false [:b :d] inc
                             true [:b :c] #(if % inc 1)))
        "Nominal case")))

(deftest concat-when-test
  (testing "concat-when"
    (is (= '(:a 0 :b -2 :d 4 :e :f :h)
           (c/concat-when [:a 0 :b -2 :d 4]
                          true [:e :f]
                          false [:g]
                          true (list :h)))
        "Nominal case")))

(deftest conj-when-test
  (testing "conj-when"
    (is (= [0 1]
           (c/conj-when [0]
                        true 1
                        false 2))
        "Nominal case")
    (is (= '(1 0)
           (c/conj-when (list 0)
                        true 1
                        false 2))
        "Nominal case")))

(deftest merge-when-test
  (testing "merge-when"
    (is (= {:a 1, :c 3, :b 2}
           (c/merge-when {:a 0 :c 3}
                         true {:a 1 :b 2}
                         false {:a 2 :c 2}
                         true nil))
        "Nominal case")))
