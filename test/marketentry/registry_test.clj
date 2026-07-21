(ns marketentry.registry-test
  (:require [clojure.test :refer [deftest is testing]]
            [marketentry.registry :as registry]))

(deftest engagement-fee-recompute
  (let [e {:base-fee 500000 :monthly-rate 30000 :monitoring-months 12 :claimed-fee 860000.0}]
    (is (== 860000.0 (registry/compute-engagement-fee e)))
    (is (true? (registry/engagement-fee-matches-claim? e))))
  (let [bad {:base-fee 500000 :monthly-rate 30000 :monitoring-months 12 :claimed-fee 999000.0}]
    (is (false? (registry/engagement-fee-matches-claim? bad)))))

(deftest register-draft-and-submit
  (let [d (registry/register-draft "eng-1" "BHS" 0)
        s (registry/register-submit "eng-1" "BHS" 0)]
    (is (= "BHS-DFT-000000" (get d "draft_number")))
    (is (= "BHS-SUB-000000" (get s "submit_number")))
    (is (nil? (get-in d ["certificate" "proof"])))
    (is (= "draft-unsigned" (get-in s ["certificate" "status"])))))

(deftest register-requires-ids
  (is (thrown? Exception (registry/register-draft "" "BHS" 0)))
  (is (thrown? Exception (registry/register-submit "eng-1" "" 0))))

(deftest vat-required-threshold
  (testing "at or below B$100,000 -> not required"
    (is (false? (registry/vat-required? 50000)))
    (is (false? (registry/vat-required? 100000))))
  (testing "above B$100,000 -> required"
    (is (true? (registry/vat-required? 100000.01)))
    (is (true? (registry/vat-required? 750000))))
  (testing "missing/zero turnover -> not required"
    (is (false? (registry/vat-required? nil)))
    (is (false? (registry/vat-required? 0)))))

(deftest vat-unverified
  (testing "required and verified -> not unverified"
    (is (false? (registry/vat-unverified? {:annual-turnover 750000 :vat-verified? true}))))
  (testing "not required -> not unverified regardless of the verified flag"
    (is (false? (registry/vat-unverified? {:annual-turnover 50000 :vat-verified? false}))))
  (testing "required but not verified -> unverified"
    (is (true? (registry/vat-unverified? {:annual-turnover 250000 :vat-verified? false}))))
  (testing "no turnover declared -> not required, so not unverified"
    (is (false? (registry/vat-unverified? {:vat-verified? false})))))

(deftest business-licence-unverified
  (testing "verified -> not unverified"
    (is (false? (registry/business-licence-unverified? {:business-licence-verified? true}))))
  (testing "unverified -> unverified, regardless of turnover"
    (is (true? (registry/business-licence-unverified? {:business-licence-verified? false}))))
  (testing "missing flag entirely -> treated as unverified"
    (is (true? (registry/business-licence-unverified? {})))))
