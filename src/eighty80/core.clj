(ns eighty80.core
  (:gen-class)
  (:require [eighty80.utils :refer :all]
            [eighty80.opcodes :refer :all]))

(defn eighty80 [state]
  "The emulator program loop."
  (println (take 10 (:memory @state)))
  (loop [op-code (aget (:memory @state) (:pc @state))]
    (println "---------------------")
    (println @state)
    (printf (str  "Opcode: 0x%02x\n") op-code)
    ((get op-codes op-code) state)
    (recur (aget (:memory @state) (:pc @state)))))

(defn -main
  "Runs the 8080 chipset emulator"
  [& args]
  (def initial-state (atom {:a 0 :b 0 :c 0 :d 0 :e 0 :h 0 :l 0
                        :sp 0
                        :pc 0
                        :memory (create-memory (first args))
                        :codes {:z 0 :s 0 :p 0 :cy 0 :ac 0 :pad 0}
                        :int_enable 0
                    }))
   (eighty80 initial-state))
