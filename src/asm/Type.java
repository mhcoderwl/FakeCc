package asm;

//生成中间代码的Type的
public enum Type {
    INT8, INT16, INT32, INT64;
    //按照类型的所占的字节大小来返回对应的汇编类型.
    static public Type get(long size) {
        switch ((int)size) {
        case 1:
            return INT8;
        case 2:
            return INT16;
        case 4:
            return INT32;
        case 8:
            return INT64;
        default:
            throw new Error("unsupported asm type size: " + size);
        }
    }

    public int size() {
        switch (this) {
        case INT8:
            return 1;
        case INT16:
            return 2;
        case INT32:
            return 4;
        case INT64:
            return 8;
        default:
            throw new Error("must not happen");
        }
    }
}
