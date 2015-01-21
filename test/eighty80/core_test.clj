(ns eighty80.core-test
  (:require [clojure.test :refer :all]
            [eighty80.core :refer :all]
            [eighty80.opcodes :refer :all]
            [eighty80.utils :refer :all]
            ))

(defn get-blank-state []
  "Generates a blank state"
  (atom {:a 0 :b 0 :c 0 :d 0 :e 0 :h 0 :l 0
         :sp 0
         :pc 0
         :memory (create-memory)
         :codes {:z 0 :s 0 :p 0 :cy 0 :ac 0 :pad 0}
         :int_enable 0
         })
  )

(deftest test-PCHL
  (testing "PCHL moves the pc to the location stored in HL"
    (def state (get-blank-state))
    (swap! state assoc :h 0xCA)
    (swap! state assoc :l 0xFE)
    (PCHL state)
    (is (= (:pc @state) 0xCAFE))))

(deftest test-JMP
  (testing "JMP moves the PC to the next place"
    (def state (get-blank-state))
    (aset-int (:memory @state) 1 0xC3)
    (aset-int (:memory @state) 2 0x37)
    (JMP state)
    (is (= (:pc @state) 0x37C3))))

(deftest test-JC [state]
  (def state (get-blank-state))
  (testing "Jump If Carry - If Carry bit is one then Jump"
    (swap! state assoc-in [:codes] {:cy 1})
    (aset-int (:memory @state) 1 0xFE)
    (aset-int (:memory @state) 2 0xCA)
    (JC state)
    (is (= (:pc @state) 0xCAFE)))
  (testing "Doesnt Jump if Carry bit is zero"
    (swap! state assoc-in [:codes] {:cy 0})
    (JC state)
    (is (= (:pc @state) 51969))))

(deftest test-JNC [state]
  (def state (get-blank-state))
  (testing "Jump If No Carry - If Carry bit is zero then Jump"
    (swap! state assoc-in [:codes] {:cy 0})
    (aset-int (:memory @state) 1 0xFE)
    (aset-int (:memory @state) 2 0xCA)
    (JNC state)
    (is (= (:pc @state) 0xCAFE)))
  (testing "Doesnt Jump if Carry bit is zero"
      (swap! state assoc-in [:codes] {:cy 1})
      (JNC state)
      (is (= (:pc @state) 51969))))

(defn test-JZ [state]
  (testing "Jump If Zero - If Zero bit is one then Jump"))

(defn test-JNZ [state]
  (testing "Jump If Not Zero - If Zero bit is zero then Jump"))

(defn test-JM [state]
  (testing "Jump If Minus - If Sign bit is one then Jump"))

(defn test-JP [state]
  (testing "Jump If Positive - If Sign bit is zero then Jump"))

(defn test-JPE [state]
  (testing "Jump If Parity Even - If Parity bit is one then Jump"))

(defn test-JPO [state]
  (testing "Jump If Parity Odd - If Parity bit is zero then Jump"))

(deftest test-LXI-SP
  (testing "next two bytes are loaded into the sp buffer.  PC is incremented 3."
    (def state (get-blank-state))
    (aset-int (:memory @state) 1 0xFE)
    (aset-int (:memory @state) 2 0xCA)
    (LXI-SP state)
    (is (= (:pc @state) 3))
    (is (= (:sp @state) 0xCAFE))))

(deftest test-LXI-B
  (testing "next two bytes are loaded into b and c respectivly.  PC is incremented 3."
    (def state (get-blank-state))
    (aset-int (:memory @state) 1 0xFE)
    (aset-int (:memory @state) 2 0xCA)
    (LXI-B state)
    (is (= (:pc @state) 3))
    (is (= (:b @state) 0xCA))
    (is (= (:c @state) 0xFE))))

(deftest test-LXI-D
  (testing "next two bytes are loaded into d and e respectivly.  PC is incremented 3."
    (def state (get-blank-state))
    (aset-int (:memory @state) 1 0xFE)
    (aset-int (:memory @state) 2 0xCA)
    (LXI-D state)
    (is (= (:pc @state) 3))
    (is (= (:d @state) 0xCA))
    (is (= (:e @state) 0xFE))))

(deftest test-LXI-H
  (testing "next two bytes are loaded into h and l respectivly buffer.  PC is incremented 3."
    (def state (get-blank-state))
    (aset-int (:memory @state) 1 0xFE)
    (aset-int (:memory @state) 2 0xCA)
    (LXI-H state)
    (is (= (:pc @state) 3))
    (is (= (:h @state) 0xCA))
    (is (= (:l @state) 0xFE))))
