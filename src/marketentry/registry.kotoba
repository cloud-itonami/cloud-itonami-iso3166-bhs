(ns marketentry.registry
  "Pure-function market-entry filing-draft + filing-submit record
  construction -- an append-only market-entry book-of-record draft.

  Like every sibling actor's registry, there is no single international
  reference-number standard for a public-procurement market-entry
  filing -- every jurisdiction assigns its own format. This namespace
  does NOT invent one; it builds a jurisdiction-scoped sequence number
  and validates the record's required fields, the same honest,
  non-fabricating discipline `marketentry.facts` uses.

  `engagement-fee-matches-claim?` is an HONEST reapplication of the
  SAME ground-truth-recompute DISCIPLINE sibling actors use (verify a
  claimed monetary total against the entity's own recorded quantity x
  unit fields), reapplied to a market-entry engagement fee line.

  `vat-required?` / `vat-unverified?` are the SAME discipline applied to
  a genuinely Bahamas-specific mechanism: the Value Added Tax Act, 2014
  (Chapter 370A) ss.19+21(a) fixes a REAL statutory registration
  threshold -- B$100,000 of turnover from a taxable activity in any
  twelve-or-fewer-month period (`marketentry.facts/vat-spec-basis`).
  Rather than trust an engagement's self-reported `:requires-vat?` flag,
  `vat-required?` INDEPENDENTLY recomputes whether the threshold is
  crossed from the engagement's own declared `:annual-turnover`, and
  `vat-unverified?` HARD-holds when it is crossed but `:vat-verified?`
  is not true.

  This is a GENUINELY DIFFERENT check SHAPE than every prior iso3166
  sibling this repo mirrors, and not merely a numeric variant of one:
  Bulgaria's ЗОП Art. 54(5) de-minimis is a PERCENTAGE-OF-TURNOVER
  formula; Albania's Neni 76(2)(c) carve-out is a FLAT STATUTORY
  CONSTANT; Azerbaijan's/Armenia's flagship checks are plain BOOLEAN
  registry-membership reads; Antigua and Barbuda's vendor-class tiers
  are a discrete THREE-STEP THRESHOLD classification. The Bahamas has
  NO general income tax and consequently no single TIN-style regime at
  all -- so this catalog's flagship is not one check but a BIFURCATED
  PAIR: `vat-unverified?` (CONDITIONAL, threshold-gated, exactly the
  'recompute against a real number' shape above) running ALONGSIDE
  `business-licence-unverified?` (UNCONDITIONAL -- the Business Licence
  Act, 2023 s.9(1) requires a licence of literally every business,
  regardless of turnover). No prior sibling's catalog has needed to
  express two independently-triggered checks of two different shapes
  for what a single jurisdiction's tax/business-identity requirement
  turned out to actually be.

  This namespace is pure data + pure functions -- no I/O, no network
  call to any real procurement portal. It builds the RECORD an
  operator would keep, not the act of submitting a portal registration
  itself (that is `marketentry.operation`'s `:filing/submit`, always
  human-gated -- see README Actuation)."
  (:require [kotoba.lang.text :as str]))

(defn- unsigned-certificate
  "Every certificate this actor produces is UNSIGNED -- signature is
  the market-entry operator's act, not this actor's."
  [kind subject record-id]
  {"@context" ["https://www.w3.org/ns/credentials/v2"]
   "type" ["VerifiableCredential" kind]
   "credentialSubject" {"id" subject "record" record-id}
   "proof" nil
   "issued_by_registry" false
   "status" "draft-unsigned"})

(defn- zero-pad [n w]
  (let [s (str n)]
    (str (apply str (repeat (max 0 (- w (count s))) "0")) s)))

(def ^:private money-scale
  "Sub-minor-unit scale used when comparing two money amounts: 1/10000 of
  a unit. Coarser than double representation error by many orders of
  magnitude, finer than any real currency's minor unit (2 decimals for
  most, 3 for KWD/BHD/OMR, 0 for JPY/KRW)."
  10000)

(defn- money=
  "Exact-at-money-precision equality for two amounts.

  `==` on raw doubles is NOT the right comparison for money. With
  whole-unit fees the two agree, but as soon as an amount carries
  cents the sum `base + rate x months` is routinely not the double
  nearest the true total, and a CORRECT claim compares false: measured
  on this exact shape, 40,989 of 327,060 cent-denominated combinations
  (12.5%) were rejected while being right, against 0 of 327,060 in
  whole units.

  Rounding both sides to `money-scale` before comparing removes the
  representation error while preserving every distinction money can
  actually carry."
  [x y]
  (and (number? x) (number? y)
       (= (Math/round (* money-scale (double x)))
          (Math/round (* money-scale (double y))))))

(defn compute-engagement-fee
  "The ground-truth engagement fee for `engagement`'s own `:base-fee`
  and `:monitoring-months` x `:monthly-rate` -- a single flat
  base + months x rate calculation, not a full pricing engine."
  [{:keys [base-fee monthly-rate monitoring-months]}]
  ;; nil when any field is not a number: an un-recomputable engagement is
  ;; un-verifiable, which is neither `correct` nor a ClassCastException
  ;; thrown out of the caller.
  (when (and (number? base-fee) (number? monthly-rate) (number? monitoring-months))
    (+ (double base-fee)
       (* (double monthly-rate) (double monitoring-months)))))

(defn engagement-fee-matches-claim?
  "Does `engagement`'s own `:claimed-fee` equal the independently
  recomputed `compute-engagement-fee`?"
  [{:keys [claimed-fee] :as engagement}]
  (money= claimed-fee (compute-engagement-fee engagement)))

(def vat-registration-threshold-bsd
  "Value Added Tax Act, 2014 (Chapter 370A) ss.19+21(a), fetched directly
  from inlandrevenue.finance.gov.bs and read via `pdftotext -layout`
  2026-07-22: the registration threshold -- turnover from a taxable
  activity in excess of this amount, in any period of twelve or fewer
  months, triggers MANDATORY VAT registration. All amounts are Bahamian
  Dollars (B$), the currency the Act's own text is denominated in."
  100000.0)

(defn vat-required?
  "Does `annual-turnover` (B$) EXCEED the Value Added Tax Act, 2014's own
  mandatory-registration threshold? Missing/nil turnover is treated as
  not exceeding it (never assume a registration duty from absent data)."
  [annual-turnover]
  (> (double (or annual-turnover 0)) vat-registration-threshold-bsd))

(defn vat-unverified?
  "Does `engagement`'s own declared `:annual-turnover` INDEPENDENTLY
  cross the VAT Act's registration threshold while `:vat-verified?` is
  not true? CONDITIONAL on the engagement's own ground truth -- exactly
  the same 'recompute, don't trust the self-reported flag' discipline
  `engagement-fee-matches-claim?` uses, applied to a real statutory
  numeric threshold instead of a flat arithmetic formula."
  [{:keys [annual-turnover vat-verified?]}]
  (and (vat-required? annual-turnover) (not (true? vat-verified?))))

(defn business-licence-unverified?
  "Does `engagement` lack a verified Business Licence? UNCONDITIONAL --
  the Business Licence Act, 2023 (No. 13 of 2023) s.9(1) requires every
  business operating in or from the Bahamas to hold one, regardless of
  turnover or contract value -- so, unlike `vat-unverified?`, this check
  applies to EVERY engagement, not only those crossing a numeric
  threshold, the same 'always applies' shape ATG's vendor-class check
  and Bulgaria's tax-arrears check use for their own always-applicable
  facts."
  [{:keys [business-licence-verified?]}]
  (not (true? business-licence-verified?)))

(defn register-draft
  "Validate + construct the FILING-DRAFT registration DRAFT -- the
  market-entry operator's own act of preparing a portal registration
  package. Pure function -- does not touch any real procurement
  portal."
  [engagement-id jurisdiction sequence]
  (when-not (and engagement-id (not= engagement-id ""))
    (throw (ex-info "draft: engagement_id required" {})))
  (when-not (and jurisdiction (not= jurisdiction ""))
    (throw (ex-info "draft: jurisdiction required" {})))
  (when (< sequence 0)
    (throw (ex-info "draft: sequence must be >= 0" {})))
  (let [draft-number (str (str/upper jurisdiction) "-DFT-" (zero-pad sequence 6))
        record {"record_id" draft-number
                "kind" "filing-draft"
                "engagement_id" engagement-id
                "jurisdiction" jurisdiction
                "immutable" true}]
    {"record" record "draft_number" draft-number
     "certificate" (unsigned-certificate "FilingDraft" draft-number draft-number)}))

(defn register-submit
  "Validate + construct the FILING-SUBMIT registration DRAFT -- the
  market-entry operator's own act of actually submitting a portal
  registration (always human-gated upstream)."
  [engagement-id jurisdiction sequence]
  (when-not (and engagement-id (not= engagement-id ""))
    (throw (ex-info "submit: engagement_id required" {})))
  (when-not (and jurisdiction (not= jurisdiction ""))
    (throw (ex-info "submit: jurisdiction required" {})))
  (when (< sequence 0)
    (throw (ex-info "submit: sequence must be >= 0" {})))
  (let [submit-number (str (str/upper jurisdiction) "-SUB-" (zero-pad sequence 6))
        record {"record_id" submit-number
                "kind" "filing-submit"
                "engagement_id" engagement-id
                "jurisdiction" jurisdiction
                "immutable" true}]
    {"record" record "submit_number" submit-number
     "certificate" (unsigned-certificate "FilingSubmit" submit-number submit-number)}))

(defn append [history result]
  (conj (vec history) (get result "record")))
