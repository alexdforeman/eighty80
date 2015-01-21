(ns eighty80.opcodes
  (:require [eighty80.utils :refer :all]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; NOP Instructions.
(defn NOP [state]
  "NOP is a no operation on anything. Just advances the program counter by one."
  (swap! state assoc :pc (inc (:pc @state))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Jump Instructions.
(defn PCHL [state]
  "Load the PC with the HL registers and continue from there"
  (let [low (:l @state)
        high (:h @state)
        new-pc (to-16-bit-addr low high)]
    (swap! state assoc :pc new-pc))
  )

(defn generic-jump [state test]
  "All other jump codes either do a check and then use the next two bits, or they increment by 3.
   This is easy to make Generic and just call from here."
  (if test
    (let [pc (:pc @state)
          low (aget (:memory @state) (+ pc 1))
          high (aget (:memory @state) (+ pc 2))
          new-pc (to-16-bit-addr low high)]
      (swap! state assoc :pc new-pc))
    ;; Else
    (swap! state assoc :pc (+ 3 (:pc @state)))))

(defn JMP [state]
  "Jumps the PC unconditionally to the next location."
  (generic-jump state true))

(defn JC [state]
  "Jump If Carry - If Carry bit is one then Jump"
  (generic-jump state (= 1 (:cy (:codes @state)))))

(defn JNC [state]
  "Jump If No Carry - If Carry bit is zero then Jump"
  (generic-jump state (= 0 (:cy (:codes @state)))))

(defn JZ [state]
  "Jump If Zero - If Zero bit is one then Jump"
  (generic-jump state (= 1 (:z (:codes @state)))))

(defn JNZ [state]
  "Jump If Not Zero - If Zero bit is zero then Jump"
  (generic-jump state (= 0 (:z (:codes @state)))))

(defn JM [state]
  "Jump If Minus - If Sign bit is one then Jump"
  (generic-jump state (= 1 (:s (:codes @state)))))

(defn JP [state]
  "Jump If Positive - If Sign bit is zero then Jump"
  (generic-jump state (= 0 (:s (:codes @state)))))

(defn JPE [state]
  "Jump If Parity Even - If Parity bit is one then Jump"
  (generic-jump state (= 1 (:p (:codes @state)))))

(defn JPO [state]
  "Jump If Parity Odd - If Parity bit is zero then Jump"
  (generic-jump state (= 0 (:p (:codes @state)))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Start of Immediate Instructions
(defn LXI-SP [state]
  "Load the contents of SP register with the next two bytes"
  (let [pc (:pc @state)
        low (aget (:memory @state) (+ pc 1))
        high (aget (:memory @state) (+ pc 2))
        new-sp (to-16-bit-addr low high)]
    (swap! state assoc :sp new-sp
           :pc (+ pc 3))))

(defn LXI-B [state]
  "Load the contents of B register byte 2 and C with byte 1"
  (let [pc (:pc @state)]
    (swap! state assoc :b (aget (:memory @state) (+ pc 2))
           :c (aget (:memory @state) (+ pc 1))
           :pc (+ pc 3))))

(defn LXI-D [state]
  "Load the contents of D register byte 2 and E with byte 1"
  (let [pc (:pc @state)]
    (swap! state assoc :d (aget (:memory @state) (+ pc 2))
           :e (aget (:memory @state) (+ pc 1))
           :pc (+ pc 3))))

(defn LXI-H [state]
  "Load the contents of H register byte 2 and L with byte 1"
  (let [pc (:pc @state)]
    (swap! state assoc :h (aget (:memory @state) (+ pc 2))
           :l (aget (:memory @state) (+ pc 1))
           :pc (+ pc 3))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; MAP of all codes to functions.
(def op-codes
  "Map of all the opcode numbers to the function we want to use."
  {0x00 NOP
   0x31 LXI-SP
   0xC2 JNZ
   0xCA JZ
   0xC3 JMP
   0xD2 JNC
   0xDA JC
   0xE2 JPO
   0xEA JPE
   0xE9 PCHL
   0xF2 JP
   0xFA JM})
