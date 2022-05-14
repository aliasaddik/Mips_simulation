public class FetchInstruction {


     public static String fetch(String PC){
         return  controller.Instruction.memory[Integer.parseInt(PC,2)];
     }
}
