(ns marketentry.governor
  "Market-Entry Compliance Governor -- the independent compliance layer
  that earns the MarketEntry-LLM the right to commit. The LLM has no
  notion of Bahamian procurement law, whether a claimed engagement fee
  actually equals base + months x rate, whether a Business Licence has
  been verified for an engagement (unconditionally required by the
  Business Licence Act, 2023), whether VAT registration has been
  verified where the engagement's own declared turnover crosses the
  Value Added Tax Act, 2014's B$100,000 threshold, or when a draft stops
  being a draft and becomes a real-world mofvendors.gov.bs portal
  submission, so this MUST be a separate system able to *reject* a
  proposal and fall back to HOLD.

  `:itonami.blueprint/governor` is `:market-entry-compliance-governor`
  (shared family keyword on blueprints).

  This blueprint's own text (docs/business-model.md Trust Controls:
  'any actual portal registration or filing submission requires
  Market-Entry Compliance Governor clearance and always escalates to
  human sign-off'; 'a false or fabricated regulatory-requirement claim
  is a HARD hold') names exactly the checks below.

  Seven checks, in priority order, ALL HARD violations: a human
  approver CANNOT override them. The confidence/actuation gate is
  SOFT: it asks a human to look (low confidence / actuation), and the
  human may approve -- but see `marketentry.phase`: for `:stake
  :actuation/draft-filing`/`:actuation/submit-filing` NO phase ever
  allows auto-commit either. Two independent layers agree that
  actuation is always a human call.

    1. Spec-basis                  -- did the jurisdiction proposal cite
                                       an OFFICIAL source
                                       (`marketentry.facts`), or invent
                                       one?
    2. Evidence incomplete         -- for `:filing/draft`/
                                       `:filing/submit`, has the
                                       jurisdiction actually been
                                       assessed with a full evidence
                                       checklist on file?
    3. Business Licence unverified -- for `:filing/submit`,
                                       UNCONDITIONALLY (the Business
                                       Licence Act, 2023 s.9(1) requires
                                       every business to hold one,
                                       regardless of turnover) check the
                                       engagement's own
                                       `:business-licence-verified?`.
                                       Half of this vertical's flagship
                                       bifurcated check -- see
                                       `marketentry.registry`.
    4. Engagement fee mismatch     -- for `:filing/submit`,
                                       INDEPENDENTLY recompute whether
                                       the engagement's own `:claimed-
                                       fee` equals `base-fee +
                                       monthly-rate x monitoring-
                                       months` -- honest reapplication
                                       of the ground-truth-recompute
                                       discipline sibling actors use.
    5. VAT unverified               -- for `:filing/submit`,
                                       INDEPENDENTLY recompute whether
                                       the engagement's own declared
                                       `:annual-turnover` crosses the
                                       Value Added Tax Act, 2014's
                                       B$100,000 threshold, and if so
                                       whether `:vat-verified?` is true.
                                       CONDITIONAL on the engagement's
                                       own ground truth -- the other
                                       half of the bifurcated check; the
                                       Bahamas has no general income tax,
                                       so there is no TIN-style regime
                                       here, only this threshold-gated
                                       VAT-registration duty alongside
                                       the unconditional Business Licence
                                       duty above.
    6. Confidence floor / actuation
       gate                          -- LLM confidence below threshold,
                                       OR the op is `:filing/draft`/
                                       `:filing/submit` (REAL acts)
                                       -> escalate.

  Two more guards, double-draft/double-submit prevention, are enforced
  off dedicated `:drafted?`/`:submitted?` facts (never a `:status`
  value)."
  (:require [marketentry.facts :as facts]
            [marketentry.registry :as registry]
            [marketentry.store :as store]))

(def confidence-floor 0.6)

(def high-stakes
  "Stakes grave enough to always require a human, even when clean.
  Drafting a real portal package and submitting a real portal
  registration are the two real-world actuation events this actor
  performs."
  #{:actuation/draft-filing :actuation/submit-filing})

;; ----------------------------- checks -----------------------------

(defn- spec-basis-violations
  "A `:jurisdiction/assess` (or `:filing/draft`/`:filing/submit`)
  proposal with no spec-basis citation is a HARD violation -- never
  invent a jurisdiction's market-entry requirements."
  [{:keys [op]} proposal]
  (when (contains? #{:jurisdiction/assess :filing/draft :filing/submit} op)
    (let [value (:value proposal)]
      (when (or (empty? (:cites proposal))
                (and (contains? value :spec-basis) (nil? (:spec-basis value))))
        [{:rule :no-spec-basis
          :detail "公式spec-basisの引用が無い提案は法域要件として扱えない"}]))))

(defn- evidence-incomplete-violations
  "For `:filing/draft`/`:filing/submit`, the jurisdiction's required
  registration evidence must actually be satisfied."
  [{:keys [op subject]} st]
  (when (contains? #{:filing/draft :filing/submit} op)
    (let [e (store/engagement st subject)
          assessment (store/assessment-of st subject)]
      (when-not (and assessment
                     (facts/required-evidence-satisfied?
                      (:jurisdiction e) (:checklist assessment)))
        [{:rule :evidence-incomplete
          :detail "法域の必要書類(会社登記/ビジネスライセンス/VAT登録確認等)が充足していない状態での提案"}]))))

(defn- business-licence-unverified-violations
  "For `:filing/submit`, UNCONDITIONALLY check `registry/business-
  licence-unverified?` -- the Business Licence Act, 2023 s.9(1) requires
  every business to hold a licence regardless of turnover, so this is
  evaluated for EVERY `:filing/submit`, not gated behind any
  `:requires-X?` engagement flag, the same 'always applies' shape
  Bulgaria's tax-arrears check and Azerbaijan's/Armenia's registry-
  membership checks use."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (let [e (store/engagement st subject)]
      (when (registry/business-licence-unverified? e)
        [{:rule :business-licence-unverified
          :detail (str subject " はビジネスライセンス(Business Licence Act, 2023 s.9(1))未確認 -- 提出提案は進められない")}]))))

(defn- engagement-fee-mismatch-violations
  "For `:filing/submit`, INDEPENDENTLY recompute whether the
  engagement's own claimed fee equals base + months x rate."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (let [e (store/engagement st subject)]
      (when-not (registry/engagement-fee-matches-claim? e)
        [{:rule :engagement-fee-mismatch
          :detail (str subject " の申告手数料(" (:claimed-fee e)
                      ")が独立再計算値(" (registry/compute-engagement-fee e) ")と一致しない")}]))))

(defn- vat-unverified-violations
  "For `:filing/submit`, INDEPENDENTLY recompute (via `registry/vat-
  required?`) whether the engagement's own declared `:annual-turnover`
  crosses the Value Added Tax Act, 2014's B$100,000 threshold, and
  HARD-hold if so while `:vat-verified?` is not true. CONDITIONAL on the
  engagement's own ground truth, the other half of the bifurcated check
  that replaces every prior sibling's single TIN-style check (the
  Bahamas has no general income tax)."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (let [e (store/engagement st subject)]
      (when (registry/vat-unverified? e)
        [{:rule :vat-unverified
          :detail (str subject " は申告売上(B$" (:annual-turnover e)
                      ")がVAT登録閾値(B$" registry/vat-registration-threshold-bsd
                      ")を超えているがVAT登録未確認 -- 提出提案は進められない")}]))))

(defn- already-drafted-violations
  "For `:filing/draft`, refuses to draft the SAME engagement twice."
  [{:keys [op subject]} st]
  (when (= op :filing/draft)
    (when (store/engagement-already-drafted? st subject)
      [{:rule :already-drafted
        :detail (str subject " は既にドラフト済み")}])))

(defn- already-submitted-violations
  "For `:filing/submit`, refuses to submit the SAME engagement twice."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (when (store/engagement-already-submitted? st subject)
      [{:rule :already-submitted
        :detail (str subject " は既に提出済み")}])))

(defn check
  "Censors a MarketEntry-LLM proposal against the governor rules.
  Returns {:ok? bool :violations [..] :confidence c :escalate? bool
  :high-stakes? bool :hard? bool}."
  [request _context proposal st]
  (let [hard (into []
                   (concat (spec-basis-violations request proposal)
                           (evidence-incomplete-violations request st)
                           (business-licence-unverified-violations request st)
                           (engagement-fee-mismatch-violations request st)
                           (vat-unverified-violations request st)
                           (already-drafted-violations request st)
                           (already-submitted-violations request st)))
        conf (:confidence proposal 0.0)
        low? (< conf confidence-floor)
        stakes? (boolean (high-stakes (:stake proposal)))
        hard? (boolean (seq hard))]
    {:ok?          (and (not hard?) (not low?) (not stakes?))
     :violations   hard
     :confidence   conf
     :hard?        hard?
     :escalate?    (and (not hard?) (or low? stakes?))
     :high-stakes? stakes?}))

(defn hold-fact
  "The audit fact written when a proposal is rejected (HOLD)."
  [request context verdict]
  {:t          :governor-hold
   :op         (:op request)
   :actor      (:actor-id context)
   :subject    (:subject request)
   :disposition :hold
   :basis      (mapv :rule (:violations verdict))
   :violations (:violations verdict)
   :confidence (:confidence verdict)})
