(ns eighty80.utils
    (:import [java.io DataInputStream FileInputStream File]))

(def SIZE_OF_8080_MEMORY 0xFFFF)

(defn create-memory
  ([]
  "Generates a memory space which is empty"
  (int-array SIZE_OF_8080_MEMORY))
  ([file]
  "Creates the memory and also fake converts it to unsigned bytes."
  (def rawMemory (byte-array SIZE_OF_8080_MEMORY))
  (-> file
      (File.)
      (FileInputStream.)
      (DataInputStream.)
      (.read rawMemory))
  (into-array Integer/TYPE (map #(bit-and % 0xFF) rawMemory))
  ))

(defn to-16-bit-addr [low high]
  "Takes the low then the high pieces of memory and converts it into a 16 bit number from two 8 bit numbers"
  (bit-or (bit-shift-left high 8) low))
