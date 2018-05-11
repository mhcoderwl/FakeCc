package ir;

import ast.*;
import asm.*;
/*
 * switch包含的跳转条件和跳转目标
 */
public class Case implements Dumpable {
    public long value;
    public Label label;

    public Case(long value, Label label) {
        this.value = value;
        this.label = label;
    }

    public void dump(Dumper d) {
        d.printClass(this);
        d.printMember("value", value);
        d.printMember("label", label);
    }
}
