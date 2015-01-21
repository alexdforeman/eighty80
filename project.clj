(defproject eighty80 "0.1.0-SNAPSHOT"
  :description "An 8080 emulator"
  :url "http://example.com/FIXME"
  :license {:name "The BSD 3-Clause License"
            :url "http://opensource.org/licenses/BSD-3-Clause"}
  :dependencies [[org.clojure/clojure "1.6.0"]]
  ;;   :main ^:skip-aot eighty80.disassembler
  :aot [eighty80.core eighty80.disassembler]
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :disassemble {:main eighty80.disassembler}
             :eighty80 {:main eighty80.core}
             }
  :aliases {"eighty80"    ["with-profile" "eighty80" "run"]
            "disassemble" ["with-profile" "disassemble" "run"]})
