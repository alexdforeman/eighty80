(ns eighty80.disassembler
  (:gen-class)
  (:import [java.io DataInputStream FileInputStream File])
  (:require [clojure.string :as cstr])
  (:require [clojure.java.io :as io]))

;;; The opcode defrecord.
(defrecord opCode [opCode description size flags instr])

(defn disassemble [& args]
  "Disassembles an 8080 Program file, and Pretty prints it"
  (def data (DataInputStream. (FileInputStream. (File. (first args))))) ; The data file.
  (def opCodes                          ;Grab all the opcodes and place them in a map for use.
    (loop [opCodes (map #(cstr/split % #":")
                        (cstr/split (slurp (-> "Opcodes-8080" io/resource io/file) ) #"\n" ))
           map {}]
      (cond (empty? opCodes) map
            :else (let [data (first opCodes)
                        value (first data)
                        opCode (opCode. value (second data) (nth data 2) (nth data 3) (last data))]
                    (recur (rest opCodes) (into map {(keyword value) opCode}))))))
  ;;; Now the main part.
  (loop [byteCode (.read data)
         line 0]
    (cond (< byteCode 0) (println "Finished!")
          :else (let [byte (format "0x%02x" byteCode)
                      opCode ((keyword byte) opCodes)
                      sizeString (if (cstr/blank? (:size opCode)) "1" (:size opCode))
                      size (Integer/parseInt sizeString) ]
                  (cond (= 1 size) (do
                                     (printf  (str "%04x | " byte " | %11s |       | %15s | %47s |%n")
                                              line (:description opCode) (:flags opCode) (:instr opCode))
                                     (recur (.read data) (+ 1 line) ))
                        (= 2 size) (do
                                     (printf  (str "%04x | " byte " | %11s | "
                                                   (format "%02x" (.read data)) "    | %15s | %47s |%n" )
                                              line (:description opCode) (:flags opCode) (:instr opCode))
                                     (recur (.read data) (+ 2 line) ))
                        (= 3 size) (do
                                     (printf  (str "%04x | " byte " | %11s | "
                                                   (format "%02x" (.read data)) " "
                                                   (format "%02x" (.read data)) " | %15s | %47s |%n" )
                                              line (:description opCode) (:flags opCode) (:instr opCode))
                                     (recur (.read data) (+ 3 line) )))))))

(defn -main [& args]
  "Call with the file name you want to disassemble.  e.g. '/home/alex/si/invaders.h'"
  (disassemble (first args))
  )
