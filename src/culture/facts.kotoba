(ns culture.facts
  "Country-level regional-culture catalog for the Bahamas (BHS) -- national
  dishes, protected products, beverages, crafts, festivals and heritage
  sites, per ADR-2607171400 addendum 2 (cloud-itonami-municipality-
  culture-catalog Wave 1, in com-junkawasaki/root). Sibling namespace to
  `marketentry.facts` / `statute.facts` (ADR-2607141700); city-level
  counterparts live in the cloud-itonami-municipality-* repos.

  Catalog is keyed by UPPERCASE ISO3 (mirrors `statute.facts`); entries
  carry no :culture/municipality (that attribute is city-level only).

  Every entry cites a source URL that was actually fetched and read on
  :culture/retrieved-at -- never fabricated. Summaries state only what the
  cited source confirms. An item not in this table has NO spec-basis, full
  stop; extend `catalog`, do not invent an id/url.")

(def catalog
  "iso3 -> vector of culture entries."
  {"BHS"
   [{:culture/id "bhs.dish.conch"
     :culture/name "Conch"
     :culture/country "BHS"
     :culture/kind :dish
     :culture/summary "Large tropical mollusk with firm, white flesh; the national dish of the Bahamas, prepared as conch salad, cracked conch, conch fritters and conch chowder."
     :culture/url "https://en.wikipedia.org/wiki/Bahamian_cuisine"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "bhs.dish.peas-and-rice"
     :culture/name "Peas and rice"
     :culture/country "BHS"
     :culture/kind :dish
     :culture/summary "Staple of Bahamian cuisine, listed among the islands' everyday dishes alongside soups served with johnny cake and grits."
     :culture/url "https://en.wikipedia.org/wiki/Bahamian_cuisine"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "bhs.dish.souse"
     :culture/name "Souse"
     :culture/country "BHS"
     :culture/kind :dish
     :culture/summary "Bahamian chicken-based soup made with lime, potatoes and pepper."
     :culture/url "https://en.wikipedia.org/wiki/Bahamian_cuisine"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "bhs.dish.guava-duff"
     :culture/name "Guava duff"
     :culture/country "BHS"
     :culture/kind :dish
     :culture/summary "Bahamian dessert dish made with fruit, especially guava, in a dough."
     :culture/url "https://en.wikipedia.org/wiki/Duff_(dessert)"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "bhs.beverage.switcha"
     :culture/name "Switcha"
     :culture/country "BHS"
     :culture/kind :beverage
     :culture/summary "Bahamian name for limeade, a lime-and-sugar drink; in the Bahamas and Turks and Caicos limeade is often referred to as switcha, also the name of a commercial Bahamian brand."
     :culture/url "https://en.wikipedia.org/wiki/Limeade"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "bhs.beverage.sky-juice"
     :culture/name "Sky juice"
     :culture/country "BHS"
     :culture/kind :beverage
     :culture/summary "Bahamian alcoholic drink combining coconut water or coconut milk, condensed or evaporated milk, and gin (or rum), optionally flavoured with nutmeg and cinnamon."
     :culture/url "https://en.wikipedia.org/wiki/Sky_juice"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "bhs.festival.junkanoo"
     :culture/name "Junkanoo"
     :culture/country "BHS"
     :culture/kind :festival
     :culture/summary "Masquerade festival of drumming, dance and parades originating during the period of African chattel slavery; in the Bahamas it dates to the 1700s, with major parades on Boxing Day and New Year's Day."
     :culture/url "https://en.wikipedia.org/wiki/Junkanoo"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}]})

(defn spec-basis [iso3] (get catalog iso3))

(defn coverage
  ([] (coverage (keys catalog)))
  ([iso3s]
   (let [have (filter catalog iso3s)
         missing (remove catalog iso3s)]
     {:requested (count iso3s)
      :covered (count have)
      :covered-jurisdictions (vec (sort have))
      :missing-jurisdictions (vec (sort missing))
      :note (str "cloud-itonami-iso3166-bhs culture catalog "
                 "(ADR-2607171400 addendum 2, Wave 1): " (count (get catalog "BHS"))
                 " BHS entries, each with a fetched-and-read citation. "
                 "Extend `culture.facts/catalog`, never fabricate an id/url.")})))

(defn by-kind [iso3 kind]
  (filterv #(= (:culture/kind %) kind) (spec-basis iso3)))
