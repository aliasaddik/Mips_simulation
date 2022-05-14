

import java.util.Hashtable;


public class ExecuteInstruction {

    public static  boolean  Execute(Hashtable<String,String> x){
        //System.out.println("X is: "+ x);
        //System.out.println("PC   " +controller.PC.content);
        String RD = x.get("RD");
        String R1 = x.get("R1");
        String R2 = x.get("R2");
        String IMM = x. get("IMM");

        switch ((String)x.get("Operation")){

            case("ADD"):add(RD,R1,R2,controller.registerFile, controller.SREG);return false;
            case("SUB"):sub(RD,R1,R2,controller.registerFile, controller.SREG);return false;
            case("MUL"):Mul(RD,R1,R2,controller.registerFile);return false;
            case("LDI"):LDI(RD,IMM,controller.registerFile); return false;
            case("BEQZ"):beqz(R1,IMM) ;return true ;
            case("AND"):and(RD , R1 , R2 , controller.registerFile);return false;
            case("OR"):or(RD,R1,R2,controller.registerFile);return false;
            case("JR"):jr(R1,R2);return true;
            case("SLC"):SC(false,R1,IMM,controller.registerFile,RD,controller.SREG);return false;
            case("SRC"):SC(true,R1,IMM,controller.registerFile,RD,controller.SREG);return false;
            case("LB"):LB (RD,IMM,controller.registerFile);return false;
            case("SB"):sb(R1,IMM);return false;

        }
       return false;

    }
    public static void SC (Boolean direction, String R1 ,String IMM, registerFile rf, String RD, SREG SREG){

        int result;
        System.out.println("R1: "+R1);
        int bits= controller.binToInt(R1,8);
        int immediate = controller.binToInt(IMM ,6);

        //int immediate = Integer.parseInt(IMM);

            if (direction)
                result = (bits >>> immediate) | (bits << (8 - immediate));
            else
                result = (bits << immediate | bits >>> (8 - immediate));

        if(result==0)
            SREG.Z=true;
        else{
            SREG.Z=false;
        }
        if(result<0)
            SREG.N=true;
        else{
            SREG.N=false;
        }
        String r=controller.inttoString(result,8);
        //System.out.println("Result: "+controller.inttoString(result,8));
        rf.registers.put(RD,controller.inttoString(result,8));
        System.out.println( RD + " value updated to: "+ controller.inttoString(result,8));

    }

    public static void add (String RD, String R1, String R2, registerFile rf,SREG SREG){
        int Reg1 = controller.binToInt(R1,8);
        int Reg2 = controller.binToInt(R2,8);
        int sum = Reg1 +Reg2;
        rf.registers.put(RD, controller .inttoString(sum,8) );
        System.out.println( RD + " value updated to: "+ controller .inttoString(sum,8) );
        String s = controller.inttoString(sum,8);
        int sum1= controller.binToInt(s,8);

        if (sum > Byte.MAX_VALUE)
            SREG.C= true;
        else{
            SREG.C=false;
        }
        if ((sum1<0 &&Reg1>=0 && Reg2>=0)||(sum1>=0 && Reg1<0 && Reg2<0))
            SREG.V=true;
        else{
            SREG.V=false;
        }
        if(sum<0)
            SREG.N=true;
        else{
            SREG.N=false;
        }
        SREG.S = (SREG.N)^(SREG.V) ;
        if(sum==0)
            SREG.Z=true;
        else{
            SREG.Z=false;
        }


    }
    public static void sub (String RD,String R1,String R2, registerFile rf,SREG SREG){
        int Reg1 = controller.binToInt(R1,8);
        int Reg2 = controller.binToInt(R2,8);
        int sub= Reg1 -Reg2;
        rf.registers.put( RD, controller .inttoString(sub,8) );
        System.out.println( RD + " value updated to: "+ controller .inttoString(sub,8) );
        String s = controller.inttoString(sub,8);
        int sub1= controller.binToInt(s,8);
        if (sub > Byte.MAX_VALUE)
            SREG.C= true;
        else{
            SREG.C=false;
        }
        if ((sub1<0&& Reg2<0)||(sub1>=0&&Reg2>=0))
            SREG.V=true;
        else{
            SREG.V=false;
        }
        if(sub<0)
            SREG.N=true;
        else{
            SREG.N=false;
        }
        SREG.S = (SREG.N)^(SREG.V) ;
        if(sub==0)
            SREG.Z=true;
        else{
            SREG.Z=false;
        }

    }
    public static void LB (String RD, String IMM, registerFile rf){

        String x= controller.data.memory[Integer.parseInt(IMM, 2)];
        rf.registers.put(RD,x);
        System.out.println( RD + " value updated to: "+ x);

    }


     public static void beqz(String R1, String IMM){

        if (Integer.parseInt(R1, 2)==0){
            int temp = Integer.parseInt(controller.PC.content, 2);
            temp = temp + Integer.parseInt(IMM,2);
            controller.PC.content = "0000000000"+Integer.toBinaryString(temp);


        }
    }

    public static void jr(String R1, String R2) {

        controller.PC.content = R1 + R2;
    }

    public static void LDI(String r, String imm, registerFile rf){
        if(imm.charAt(0)=='0'){
            imm = "00"+imm;
        }
        else{
            imm="11"+imm;
        }
        rf.registers.replace(r,imm);
        System.out.println( r + " value updated to: "+ imm );
    }
    public static void Mul(String rd ,String r1 ,String r2,registerFile rf ){

        int a1 = controller.binToInt(r1,8);
        int a2 = controller.binToInt(r2,8);
        int res = a1*a2;
        // 2's complement range -(2^(n-1))-> 2^(n-1)-1
        if(res > 127 || res<-128){
            controller.SREG.C=true;
        }
        if (res <0){
            controller.SREG.S = true;
        }
        if(res==0){
            controller.SREG.Z = true;
        }
        String r= controller.inttoString(res,8);
        rf.registers.replace(rd,r);
        System.out.println( rd + " value updated to: "+ r );
    }
    public static void or(String RD , String R1 , String R2 , registerFile rf){
        String r = "";
        for(int i =0;i<R1.length();i++){
            if(R1.charAt(i) == '1' || R2.charAt(i) == '1')
                r=r+"1";
            else
                r=r+"0";
        }
        controller.registerFile.registers.put(RD,r);
        System.out.println( RD + " value updated to: "+ r );
        Boolean zero = true;
        Boolean negative = false;
        if(r.charAt(0)== '1')
            negative = true;
        int k=0;
        while(k<r.length()){
            if(r.charAt(k) == '1'){
                zero = false;
                break;
            }
            k++;
        }
        controller.SREG.Z=zero;
        controller.SREG.N=negative;
    }
    public static void and(String RD , String R1 , String R2 , registerFile rf){
        String r = "";
        for(int i =0;i<R1.length();i++){
            if(R1.charAt(i) == '1' && R2.charAt(i) == '1')
                r=r+"1";
            else
                r=r+"0";
        }
        controller.registerFile.registers.put(RD,r);
        System.out.println( RD + " value updated to: "+ r );
        Boolean zero = true;
        Boolean negative = false;
        if(r.charAt(0)== '1')
            negative = true;
        int k=0;
        while(k<r.length()){
            if(r.charAt(k) == '1'){
                zero = false;
                break;
            }
            k++;
        }
        controller.SREG.Z=zero;
        controller.SREG.N=negative;
    }
    public static void sb(String R1 , String Address){

        int index = Integer.parseInt(Address,2);
        String c = controller.extend(R1, 8);
        controller.data.memory[index]=c;
        System.out.println("Data Memory Update: "+ R1 +" stored in address "+ index);

    }
    public static void main (String []args){


    }

}
