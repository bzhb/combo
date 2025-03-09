(ns combo.core
  "A small collection of useful general-purpose macro that might not look out
  of place in the clojure.core namespace."
  (:require [medley.core :as medley])
  #?(:cljs (:require-macros combo.core)))

(defmacro f-when
  "A generalisation of `cond->` to repeatedly apply conditionally a function `f`
   with threaded `expr` as first argument.
   `n-f-args` is the number of arguments of `f`, including `expr`.
   `clauses` are each composed of a `test`
   followed by the other `f` arguments `args`.
   Not expected to be used directly but rather with additional macros
   that fix the first two arguments for a specific function."
  [f n-f-args expr & clauses]
  (assert (= 0 (mod (count clauses) n-f-args)))
  (let [g (gensym)
        steps (map (fn [[test & args]] `(if ~test (~f ~g ~@args) ~g))
                   (partition n-f-args clauses))]
    `(let [~g ~expr
           ~@(interleave (repeat g) (butlast steps))]
       ~(if (empty? steps)
          g
          (last steps)))))

(defmacro assoc-when
  "Repeatedly associates a key `k`, with a value `v` in a map `m`,
   if and only if condition `c` is true.
   `ckvs` is a list of repeated `c`,`k`,`v` triplets."
  [m & ckvs]
  `(f-when assoc 3 ~m ~@ckvs))

(defmacro assoc-in-when
  "Repeatedly associates a value `v` in a nested associative structure `m`,
   where `ks` is a sequence of keys, if and only if condition `c` is true.
   `ckvs` is a list of repeated `c`,`ks`,`v` triplets."
  [m & ckvs]
  `(f-when assoc-in 3 ~m ~@ckvs))

(defmacro conj-when
  "Repeatedly conj[oin] value `x` to collection `coll`,
   if and only if condition `c` is true.
   `cxs` is a list of repeated `c`,`x` couples."
  [coll & cxs]
  `(f-when conj 2 ~coll ~@cxs))

(defmacro concat-when
  "Repeatedly concat[enate] collection `x` to collection `coll`,
   if and only if condition `c` is true.
   `cxs` is a list of repeated `c`,`x` couples."
  [coll & cxs]
  `(f-when concat 2 ~coll ~@cxs))

(defmacro dissoc-when
  "Repeatedly dissociate a key `k` in an associative structure `m`,
   if and only if condition `c` is true.
   `cks` is a list of repeated `c`,`k` couples."
  [m & cks]
  `(f-when dissoc 2 ~m ~@cks))

(defmacro dissoc-in-when
  "Repeatedly dissociate nested keys `ks` in a nested associative structure `m`,
   where `ks` is a sequence of keys, if and only if condition `c` is true.
   `cks` is a list of repeated `c`,`ks` couples."
  [m & cks]
  `(f-when medley/dissoc-in 2 ~m ~@cks))

(defmacro merge-when
  "Repeatedly merge map `m` with additonal maps,
   if and only if condition `c` is true.
   `cms` is a list of repeated `c`,`m` couples."
  [m & cms]
  `(f-when merge 2 ~m ~@cms))

(defmacro update-when
  "Repeatedly update a key `k` with a function `f`
   in an associated structure `m`, if and only if condition `c` is true.
   `ckfs` is a list of repeated `c`,`k`,`f` triplets."
  [m & ckfs]
  `(f-when update 3 ~m ~@ckfs))

(defmacro update-in-when
  "Repeatedly update with a function `f` in a nested associative structure `m`,
   where `ks` is a sequence of keys, if and only if condition `c` is true.
   `ckfs` is a list of repeated `c`,`ks`,`f` triplets."
  [m & ckfs]
  `(f-when update-in 3 ~m ~@ckfs))
