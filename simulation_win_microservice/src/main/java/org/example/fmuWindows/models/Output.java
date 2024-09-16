package org.example.fmuWindows.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Output extends Variable{
   private Double time;
   private Double output;


   public Output(Double result) {
      this.output = result;
   }
}
