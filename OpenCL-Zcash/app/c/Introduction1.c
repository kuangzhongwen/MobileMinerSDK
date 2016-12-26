//
// Created by Mr.Kuang on 10/26/16.
// 第一章－入门
//


#include <stdio.h>

/** 外部变量在外面可以省略extern */
// extern int out = 0;
int out = 0;

printCelsius() {
/* 当 fahr=0,20,... ,300 时,分别打印华氏温度与摄氏温度对照表 */
    int fahr, celsius;
    int lower = 0, upper = 300, step = 20;

    fahr = lower;

    while (fahr <= upper) {
        // celsius = 5 * (fahr-32) / 9;
        // %d代表整数
        // printf("%d\t%d\n", fahr, celsius);

        // %3d至少占3个字符宽，%6d至少占6个字符宽
        // printf("%3d\t%6d\n", fahr, celsius);

        celsius = (5.0 / 9.0) * (fahr - 32.0);
        /*
            %d 按照十进制整型数打印
            %6d 按照十进制整型数打印,至少 6 个字符宽
            %f 按照浮点数打印
            %6f 按照浮点数打印,至少 6 个字符宽
            %.2f 按照浮点数打印,小数点后有两位小数
            %6.2f 按照浮点数打印,至少 6 个字符宽,小数点后有两位小数
        */
        // 至少3个字符宽，没有小数，%6.1至少6个字符宽，有一个小数
        printf("%3.0f %6.1f\n", fahr, celsius);
        fahr += step;
    }
}

forTest() {
    for (int i = 0; i < 100; i++) {
        printf("%3d \n", i);
    }
}

/** 符号常量 */
symbolicConstants() {
    // #define 名字 替换文本(可以是字符，也可以是数字）

    /** 初始值 */
    #define INIT 0
    /** 容量 */
    #define SIZE 100

    for (int i = INIT; i < SIZE; i++) {
        printf("%3d \n", i);
    }
}

/** 字符输入/输出 */
charInOut() {
    // 标准库提供了一次读/写一个字符的函数,其中最简单的是 getchar 和 putchar 两个 函数。
    // 每次调用时,getchar 函数从文本流中读入下一个输入字符,并将其作为结果值返回。

    // int c;
    // while ((c = getchar()) != EOF) {
    //    putchar(c);
    //}


    // 字符计数
    // int count;
    // while (getchar() != EOF) {
    //    printf("%d", ++count);
    // }

    double nc;
    for (nc = 0; getchar() != EOF; nc++)
        ;
    printf("%.0f", nc);
}

/** 数组 */
array() {
    int c, i, nwhite = 0, nother = 0;
    // 定义一个整形数组
    int ndigit[10];

    while ((c = getchar()) != '~') {
        if (c >= '0' && c <= '9') {
            ++ndigit[c-'0'];
        } else if (c == ' ' || c == '\n' || c == '\t') {
            ++nwhite;
        } else {
            ++nother;
        }
    }

    printf("digits =");
    for (i = 0; i < 10; ++i) {
        printf(" %d", ndigit[i]);
    }

    printf(", white space = %d, other = %d\n",  nwhite, nother);
}

/** 函数 */
// 函数的声明，如果所要调用的函数放在调用者的后面，则需要声明以下，如果在调用者前面，无需声明
int power(int m, int n);

/** 字符数组 */
#define MAXLINE 100

int getLine(char line[], int maxLine);
void copy(char to[], char from[]);

void testCharArray() {
    int len, max = 0;
    char line[MAXLINE];
    char longest[MAXLINE];

    while ((len = getLine(line, MAXLINE)) > 0) {
        if (len > max) {
            max = len;
            copy(longest, line);
        }
    }
    if (max > 0) {
        printf("%s", longest);
    }
}

/* getline: read a line into s, return length */
int getLine(char s[], int lim) {
    int c, i;
    for (i = 0; i < lim - 1 && (c = getchar()) != 'q' && c != '\n'; ++i) {
      s[i] = c;
    }

    if (c == '\n') {
      s[i] = c;
      ++i;
    }

    // 函数把字符'\0'(即空字符,其值为 0)插入到它创建的数组的末尾,以标记 字符串的结束。这一约定已被 C 语言采用
    // :当在 C 语言程序中出现类似于"hello\0"的字符串常量时,它将以字符数组的形式存储,
    // 数组的各元素分别存储字符串的各个字符, 并以'\0'标志字符串的结束。
    s[i] = '\0';

    return i;
}

/* copy: copy 'from' into 'to'; assume to is big enough */
void copy(char to[], char from[]) {
    int i;
    i = 0;
    while ((to[i] = from[i]) != '\0') {
       ++i;
    }
}

/* 外部变量必须定义在所有函数之外,且只能定义一次,定义后编译程序将为它分配存储单元。
   在每个需要访问外部变量的函数中,必须声明相应的外部变量,此时说明其类型。
   声明时可以用 extern 语句显式声明,也可以通过上下文隐式声明。
*/

void testExtern() {
    // 外部变量放在函数里，需要加上extern声明
    extern out = 0;
    out += 10;
}

/* main函数入口 */
// 编译: gcc test.c -o test    运行: ./test
main() {
    testExtern();
    printf("%d \n", out);
    // main函数返回0表示正常执行结束，返回非0表示出现异常
    return 0;
}

/* power: raise base to n-th power; n >= 0 */
int power(int base, int n) {
    // 这些都是临时变量，但是数组作为参数，传进来的是数组的首地址，会改变其值，如果传进来的是地址，即指针，那么
    // 也会改变其值

    // 由于自动变量只在函数调用执行期间存在,因此,在函数的两次调用之间,自动变量不保留前次调用时的赋值,
    // 且在每次进入函数时都要显式为其赋值。如果自动变量没有赋值, 则其中存放的是无效值
    int i = 1, p = 1;
    for (; i <= n; ++i) {
        p = p * base;
    }
    return p;
}