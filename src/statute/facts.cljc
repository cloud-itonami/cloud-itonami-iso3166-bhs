(ns statute.facts
  "General-law compliance catalog for the Bahamas (BHS) -- extends this
  repo's existing `marketentry.facts` (public-procurement market-entry
  only, narrow scope) with a second, orthogonal catalog of statutes a
  company operating in this jurisdiction must generally track for
  compliance. Mirrors cloud-itonami-iso3166-jpn/-deu/-bgr/-aze/-alb/-arm/
  -atg/-ben/-bdi/-bfa's `statute.facts` (ADR-2607141700, cloud-itonami-
  compliance-fact-federation).

  The Bahamas is a COMMON-LAW Commonwealth realm (unlike the OHADA/
  civil-law African siblings this loop has covered) -- its own statute
  book, consolidated as the 'Statute Law of The Bahamas', is hosted at
  `laws.bahamas.gov.bs`. Every entry below cites that portal, or a
  government-department-hosted mirror of the SAME Act's own gazetted
  text -- never fabricated. WebFetch returned readable text directly on
  native (non-scanned) PDFs (e.g. the Employment Act, the Data Protection
  Act, 2025) but returned raw undecoded PDF-stream bytes on others;
  `curl -A 'Mozilla/5.0 ...'` succeeded on every attempt (HTTP 200,
  no TLS issue this time, unlike ATG's *.gov.ag hosts) and `pdftotext
  -layout` extracted clean text from every PDF actually authored as text
  (Adobe InDesign / native). Confidence is HIGH throughout: every citation
  below is the Act's own text, read directly, not a secondary summary.

  - Companies Act, Chapter 308 of the Statute Law of The Bahamas --
    confirmed via its own consolidated-chapter header ('CHAPTER 308
    COMPANIES', 'LIST OF AUTHORISED PAGES ... LRO 1/2010') and s.16
    ('Certificate of incorporation and consequences thereof'). The
    Registrar General's Department's own Registry-of-Companies
    legislation index (`rgd.gov.bs/registry-of-companies-legislation`,
    fetched directly) independently dates the Companies Act's original
    enactment to 31 July 1992 (RGD item 1992-0018), consistent with the
    chapter's own 'Original' page-authorship markers alongside later
    Law Revision Officer (LRO) amendments.
  - Employment Act, Chapter 321A -- confirmed via its own consolidated-
    chapter header ('CHAPTER 321A EMPLOYMENT') and enacting clause ('An
    Act to establish minimum standard hours of working and vacation with
    pay for employees ... [Assent 31st December, 2001] [Commencement 1st
    January, 2002]'), with the marginal citation '27 of 2001' against
    that enacting clause (further amended by S.I. 96/2003 and Act No. 25
    of 2010) -- independently corroborated by NATLEX (ILO's legislative
    database), which lists 'Bahamas - Employment Act, 2001 (No. 27 of
    2001) (CH.321A)'. The Act's own s.1 short title reads simply
    'the Employment Act'; this catalog follows NATLEX's and the
    Department of Labour's own convention of citing it with its
    enactment year for disambiguation from the separate CH.321
    Industrial Relations Act.
  - Data Protection Act, 2025 (No. 74 of 2025) -- fetched directly from
    `laws.bahamas.gov.bs` (Extraordinary Official Gazette, Nassau, 11
    December 2025), read via `pdftotext -layout`: '[Date of Assent - 9th
    December, 2025]', 'Enacted by the Parliament of The Bahamas'. Section
    101 is titled 'Repeal of No. 3 of 2003' and its body reads simply
    'repealed' -- an EXPLICIT, textual repeal of the prior Data Protection
    (Privacy of Personal Information) Act, 2003 (No. 3 of 2003). Part III
    (ss.12-23) re-establishes the Office of the Data Protection
    Commissioner ('Establishment of the Office of the Data Protection
    Commissioner' s.12, 'Independence of the Commissioner' s.14,
    'Functions of the Commissioner' s.15).

    IMPORTANT NUANCE (verify-don't-assume, same discipline this catalog
    applies to every jurisdiction's institutional structure): like the
    Public Procurement Act, 2023 and the Registrar of Companies Act, 2024
    (see `marketentry.facts` docstring), the Data Protection Act, 2025's
    own s.1(2) makes its commencement CONDITIONAL -- 'This Act shall come
    into operation on the date as the Minister may appoint by notice
    published in the Gazette, and different dates may be appointed in
    respect of different provisions' -- rather than self-executing on
    assent. This session fetched the Office of the Data Protection
    Commissioner's OWN live 'About' page (`dataprotection.gov.bs`,
    2026-07-22) directly, and its 'Vision Statement' STILL reads: 'To
    oversee the administration of the provisions of the Data Protection
    (Privacy of Personal Information) Act, 2003 and within that context to
    protect and promote privacy' -- i.e. the office's own live public
    description has not (yet, as observed) been updated to reference the
    2025 Act, consistent with either (a) the 2025 Act not yet having been
    brought into force by Ministerial notice, or (b) the live page simply
    lagging the law. This catalog cites the Data Protection Act, 2025 (the
    legally enacted, gazetted, assented successor with its own explicit
    repeal clause) as `:statute/title`, per this family's ATG precedent of
    citing an Act's own current legal name even where a portal's live
    branding lags (ATG's tendersboard.gov.ag retaining the pre-2011
    'Tenders Board' brand after its legal rename to 'Procurement Board') --
    but UNLIKE that ATG case, this is not just a brand-name lag: it may be
    a genuine not-yet-commenced-law gap. A real market entrant should
    verify current operative status directly with the Office of the Data
    Protection Commissioner before relying on either Act's specific
    section numbers for a live filing.

  A law not in this table has NO spec-basis, full stop; extend
  `catalog`, do not invent an id/url.")

(def catalog
  "iso3 -> vector of statute entries. `:statute/url` + `:statute/law-number`
  are the citation the governor requires before any compliance-fact
  proposal referencing this law can commit."
  {"BHS"
   [{:statute/id "bhs.companies-act"
     :statute/title "Companies Act"
     :statute/jurisdiction "BHS"
     :statute/kind :law
     :statute/law-number "Chapter 308 of the Statute Law of The Bahamas (originally enacted 31 July 1992)"
     :statute/url "https://laws.bahamas.gov.bs/cms/images/LEGISLATION/PRINCIPAL/1992/1992-0018/1992-0018_1.pdf"
     :statute/url-provenance :official-laws-bahamas-gov-bs
     :statute/enacted-date "1992-07-31"
     :statute/retrieved-at "2026-07-22"
     :statute/topic #{:corporate-governance :incorporation}}
    {:statute/id "bhs.employment-act"
     :statute/title "Employment Act"
     :statute/jurisdiction "BHS"
     :statute/kind :law
     :statute/law-number "No. 27 of 2001 (consolidated as CH.321A, Statute Law of The Bahamas; amended by S.I. 96/2003 and No. 25 of 2010)"
     :statute/url "https://laws.bahamas.gov.bs/cms/images/LEGISLATION/PRINCIPAL/2001/2001-0027/2001-0027.pdf"
     :statute/url-provenance :official-laws-bahamas-gov-bs
     :statute/enacted-date "2001-12-31"
     :statute/retrieved-at "2026-07-22"
     :statute/topic #{:labor :employment}}
    {:statute/id "bhs.data-protection-act"
     :statute/title "Data Protection Act, 2025"
     :statute/jurisdiction "BHS"
     :statute/kind :law
     :statute/law-number "No. 74 of 2025 -- s.101 repeals the prior Data Protection (Privacy of Personal Information) Act, 2003 (No. 3 of 2003); commencement is conditional on a Ministerial Gazette notice (s.1(2)), not yet independently confirmed by this catalog"
     :statute/url "https://laws.bahamas.gov.bs/cms/images/LEGISLATION/PRINCIPAL/2025/2025-0074/2025-0074_1.pdf"
     :statute/url-provenance :official-laws-bahamas-gov-bs
     :statute/enacted-date "2025-12-09"
     :statute/retrieved-at "2026-07-22"
     :statute/topic #{:data-protection :privacy}}]})

(defn spec-basis
  "The jurisdiction's statute vector, or nil -- nil means NO spec-basis
  for that jurisdiction yet."
  [iso3]
  (get catalog iso3))

(defn coverage
  "Honest coverage report, same shape/discipline as `marketentry.facts/coverage`:
  never report a missing jurisdiction as covered."
  ([] (coverage (keys catalog)))
  ([iso3s]
   (let [have (filter catalog iso3s)
         missing (remove catalog iso3s)]
     {:requested (count iso3s)
      :covered (count have)
      :covered-jurisdictions (vec (sort have))
      :missing-jurisdictions (vec (sort missing))
      :note (str "cloud-itonami-iso3166-bhs statute.facts Wave 0 (ADR-2607141700): "
                 (count (get catalog "BHS")) " BHS statutes seeded with an "
                 "official government-hosted citation. Extend "
                 "`statute.facts/catalog`, never fabricate a law-id or URL.")})))

(defn by-topic
  "Statutes for `iso3` tagged with `topic` (e.g. :labor, :data-protection)."
  [iso3 topic]
  (filterv #(contains? (:statute/topic %) topic) (spec-basis iso3)))
