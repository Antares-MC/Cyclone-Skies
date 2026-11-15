package com.paymentalerts.payment;


public final record Payment(String name, double amount) {


    

    

    



    public String lookupName() {
        if (!this.name.isEmpty() && (this.name.charAt(0) == '.' || this.name.charAt(0) == '*')) {
            return this.name.substring(1);
        }
        return this.name;
    }

    public String displayAmount() {
        return AmountFormat.compact(this.amount);
    }
}
