(ns marketentry.facts-test
  (:require [clojure.test :refer [deftest is testing]]
            [marketentry.facts :as facts]))

(deftest bhs-has-spec-basis
  (let [sb (facts/spec-basis "BHS")]
    (is (some? sb))
    (is (string? (:provenance sb)))
    (is (seq (:required-evidence sb)))
    (is (some? (facts/business-licence-spec-basis "BHS")))
    (is (some? (facts/vat-spec-basis "BHS")))
    (is (= 100000.0 (:vat-registration-threshold-bsd (facts/vat-spec-basis "BHS"))))))

(deftest bhs-rep-spec-basis-is-honestly-absent
  (testing "no verified personal-exclusion-grounds provision extending to a bidder's directors/officers for BHS public-procurement participation -- deliberately not claimed"
    (is (nil? (facts/rep-spec-basis "BHS")))))

(deftest unknown-jurisdiction-has-no-spec-basis
  (is (nil? (facts/spec-basis "BHZ")))
  (is (nil? (facts/spec-basis "ZZZ"))))

(deftest required-evidence-satisfied
  (let [sb (facts/spec-basis "BHS")
        all (:required-evidence sb)]
    (is (true? (facts/required-evidence-satisfied? "BHS" all)))
    (is (not (facts/required-evidence-satisfied? "BHS" (take 1 all))))
    (is (nil? (facts/required-evidence-satisfied? "BHZ" all)))))

(deftest coverage-is-honest
  (let [c (facts/coverage ["BHS" "USA" "BHZ"])]
    (is (= 3 (:requested c)))
    (is (= 2 (:covered c)))
    (is (= ["BHZ"] (:missing-jurisdictions c)))))
