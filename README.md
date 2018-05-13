# 自制编译器FakeCc
## 介绍
以C语言编译过程为基础,融合一些java的语法,用java语言编写出一门新的编程语言,使用者可以更改源代码src中的文件,按照自己喜欢的语法规则来定制.
## 语法规则的改变
1. 实现了c语言的基本数据类型,除掉了union联合体类型,增加了boolean类型;
2. 更改了c语言中数组的定义顺序,由`char a[10]`改成了`char[10] a`;
3. 实现了指针的功能,指针与指针不能进行加减运算,能和整数类型进行基本运算;
4. 实现了const,typedef等关键字的功能,删除volatile auto register关键字;
5. 结构体不能作为函数参数传入,也不能作为函数返回值;
6. 头文件导入改成了import的形式;
7. if while for goto break等循环控制语句均实现;
8. 删除了堆上分配内存的功能(allocate),内存都在函数栈上分配;
9. 默认在X86平台上编译,如果在64位下,需要使用linux操作系统的兼容模式;


## 依赖工具
前端需要使用JavaCc解析器,用来做词法分析,语法分析,抽象语法树的生成.JavaCc工具可以eclipse中直接下载该插件即可使用,只要按照javacc中定义的BNF范式规则,例如关键字,标识符,嵌套语句,该文件名称为parse.jj,位于parser文件夹下,具体使用方法可以参考:[javacc详解](https://blog.csdn.net/Newpidian/article/details/52964017).

## 代码目录结构

| **包** | **包中的类** |
| --- | --- |
| asm | 汇编对象的类 |
| ast | 抽象语法树的类 |
| compiler | Compiler类等编译器的核心类 |
| entity | 表示函数和变量等实体的类 |
| exception | 异常的类 |
| ir | 中间代码的类 |
| parser | 解析器类 |
| sysdep | 包含依赖于OS的代码的类(汇编器和链接器) |
| sysdep.x86 | 包含依赖于OS和CPU代码的类(代码生成器) |
| type | 表.fc的类型的类 |
| utils | 小的工具类 |

一个大致的流程:
1. 首先看Compiler包,其中包含了一个Compiler类,有main入口,也就是编译的入口
2. 由传入的命令行参数得到所有源文件的名字.
3. 调用build方法对源文件进行编译,首先用compile得到汇编文件.然后用assemble运行汇编器,转换为目标文件.
4. 其中compile完成到汇编代码的生成,首先用对源文件用parser得到AST,然后语义分析完成抽象语法树中语法和语义的检查.最后一个IR类生成中间代码树,然后再调用汇编类Assembler生成汇编代码.
5. 接着调用assemble对汇编代码进行链接,通过shell中的as命令将汇编文件转换成目标文件.

## 实现功能
可以打印出前端后端解析每一步的结果,比如说需要打印词法解析后每一个单词(源代码中是一个token类)的情况,可以在运行时添加参数 --print-tokens,还可以打印抽象语法树,中间代码树,汇编代码等.比如说用test文件下内的测试文件struct:
```
import stdio;

struct st {
    int[4] x;
};

int
main(void)
{
    struct st s;
    s.x[1] = 7;
    printf("%d\n", s.x[1]);
    return 0;
}

```

词法分析后的结果:
```
"import"                "import"
<SPACES>                " "
<IDENTIFIER>            "stdio"
";"                     ";"
<SPACES>                "\n\n"
"struct"                "struct"
<SPACES>                " "
<IDENTIFIER>            "st"
<SPACES>                " "
"{"                     "{"
<SPACES>                "\n    "
"int"                   "int"
"["                     "["
<INTEGER>               "4"
"]"                     "]"
<SPACES>                " "
<IDENTIFIER>            "x"
";"                     ";"
<SPACES>                "\n"
"}"                     "}"
";"                     ";"
<SPACES>                "\n\n"
"int"                   "int"
<SPACES>                "\n"
<IDENTIFIER>            "main"
"("                     "("
"void"                  "void"
")"                     ")"
<SPACES>                "\n"
"{"                     "{"
<SPACES>                "\n    "
"struct"                "struct"
<SPACES>                " "
<IDENTIFIER>            "st"
<SPACES>                " "
<IDENTIFIER>            "s"
";"                     ";"
<SPACES>                "\n    "
<IDENTIFIER>            "s"
"."                     "."
<IDENTIFIER>            "x"
"["                     "["
<INTEGER>               "1"
"]"                     "]"
<SPACES>                " "
"="                     "="
<SPACES>                " "
<INTEGER>               "7"
";"                     ";"
<SPACES>                "\n    "
<IDENTIFIER>            "printf"
"("                     "("
"\""                    "\"%d\n\""
","                     ","
<SPACES>                " "
<IDENTIFIER>            "s"
"."                     "."
<IDENTIFIER>            "x"
"["                     "["
<INTEGER>               "1"
"]"                     "]"
")"                     ")"
";"                     ";"
<SPACES>                "\n    "
"return"                "return"
<SPACES>                " "
<INTEGER>               "0"
";"                     ";"
<SPACES>                "\n"
"}"                     "}"
<SPACES>                "\n"
<EOF>                   ""
```
可以看出,词法分析能够解析出每个单词是标识符还是关键字还是空格还是整数字面量.接下来再看语法分析后输出的抽象语法树,每个源文件对应一个AST:
```
<<AST>> (test/struct3.fc:1)
variables:
functions:
    <<DefinedFunction>> (test/struct3.fc:7)
    name: "main"
    isPrivate: false
    params:
        parameters:
    body:
        <<BlockNode>> (test/struct3.fc:9)
        variables:
            <<DefinedVariable>> (test/struct3.fc:10)
            name: "s"
            isPrivate: false
            typeNode: struct st
            initializer: null
        stmts:
            <<ExprStmtNode>> (test/struct3.fc:11)
            expr:
                <<AssignNode>> (test/struct3.fc:11)
                lhs:
                    <<ArefNode>> (test/struct3.fc:11)
                    expr:
                        <<MemberNode>> (test/struct3.fc:11)
                        expr:
                            <<VariableNode>> (test/struct3.fc:11)
                            name: "s"
                        member: "x"
                    index:
                        <<IntegerLiteralNode>> (test/struct3.fc:11)
                        typeNode: int
                        value: 1
                rhs:
                    <<IntegerLiteralNode>> (test/struct3.fc:11)
                    typeNode: int
                    value: 7
            <<ExprStmtNode>> (test/struct3.fc:12)
            expr:
                <<FuncallNode>> (test/struct3.fc:12)
                expr:
                    <<VariableNode>> (test/struct3.fc:12)
                    name: "printf"
                args:
                    <<StringLiteralNode>> (test/struct3.fc:12)
                    value: "%d\n"
                    <<ArefNode>> (test/struct3.fc:12)
                    expr:
                        <<MemberNode>> (test/struct3.fc:12)
                        expr:
                            <<VariableNode>> (test/struct3.fc:12)
                            name: "s"
                        member: "x"
                    index:
                        <<IntegerLiteralNode>> (test/struct3.fc:12)
                        typeNode: int
                        value: 1
            <<ReturnNode>> (test/struct3.fc:13)
            expr:
                <<IntegerLiteralNode>> (test/struct3.fc:13)
                typeNode: int
                value: 0
```
树中每个节点的定义需要自己完成,从根节点到最后的叶子节点总共有4层继承关系.
汇编代码结构:
```
.file	"test/struct3.fc"
	.section	.rodata
.LC0:
	.string	"%d\n"
	.text
.globl main
	.type	main,@function
main:
	pushl	%ebp
	movl	%esp, %ebp
	subl	$20, %esp
	movl	$4, %eax
	imull	$1, %eax
	movl	%eax, -20(%ebp)
	leal	-16(%ebp), %eax
	addl	$0, %eax
	movl	-20(%ebp), %ecx
	addl	%ecx, %eax
	movl	%eax, %ecx
	movl	$7, %eax
	movl	%eax, (%ecx)
	movl	$4, %eax
	imull	$1, %eax
	movl	%eax, -20(%ebp)
	leal	-16(%ebp), %eax
	addl	$0, %eax
	movl	-20(%ebp), %ecx
	addl	%ecx, %eax
	movl	(%eax), %eax
	pushl	%eax
	movl	$.LC0, %eax
	pushl	%eax
	call	printf
	addl	$8, %esp
	movl	$0, %eax
	jmp	.L0
.L0:
	movl	%ebp, %esp
	popl	%ebp
	ret
	.size	main,.-main
```
其中带.的标签是汇编伪操作,由汇编器执行而不是cpu,相当于ide中的预编译.转成汇编代码后由工具**as命令**来完成链接成目标文件.

## 程序入口
主入口在compiler包中的Compiler类,可以通过--print-help查看所有的参数选项.

## 参考资料
[龙书](https://book.douban.com/subject/3296317/)

[编译器设计](https://book.douban.com/subject/20436488/)

[自制编译器](https://book.douban.com/subject/26806041/)

												_*持续更新 2018.5.12*_

