(ns interrogativ.models.spm-test
  (:use [clojure.test
         interrogativ.models.spm]))

(def line "test string for\nthe string operations")

(deftest test-remove-line
  (is (= (remove-line line)
         "the string operations")))

(deftest test-first-line
  (is (= (first-line line)
         "test string for")))

(deftest test-parse-header
  (are [header result] (= result (:value (parse-header header)))
       "# A test header" "A test header"
       "#One with no space" "One with no space"
       "#    One with too many spaces    " "One with too many spaces"
       "# One with option :option" "One with option"
       "#" ""
       "#:option" ""
       "#    :option" "")
  (are [header options] (= options (:options (parse-header header)))
       "# Header :option" '(":option")
       "# header :a :b" '(":a" ":b")
       "#:a" '("a"))
  (is (= :header (parse-header "# Header"))))

(deftest test-parse-heading
  (are [heading result] (= result (:value (parse-heading heading)))
       "## Heading" "Heading"
       "##Heading" "Heading"
       "## Heading    " "Heading")
  (are [heading h] (= h (:h (parse-heading heading)))
       "## Heading" 2
       "### Heading" 3
       "#### Heading" 4)
  (is (= :heading (:type (parse-heading "## heading")))))