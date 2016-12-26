//
// Created by Mr.Kuang on 10/27/16.
// 第六章－结构，联合
//


/* 1. 其实结构有点类似于java中的实体类。

   struct point {
       int x;
       int y;
   }

   关键字 struct 引入结构声明。
   结构声明由包含在花括号中内的一系列声明组成。关键字struct后的名字是可选的，称为结构标记。
   结构标记用于为结构命名。在定义之后,结构标记就代表花括号内的声明,可以用它作为该声明的简写形式。

   结构中定义的变量称为结构成员。结构成员，结构标记和普通变量可以采用相同的名字，它们之间不冲突。

   另外,不同结 构中的成员可以使用相同的名字,但是,从编程风格方面来说,通常只有密切相关的对象才 会使用相同的名字。

   struct就是一种数据类型。

   struct 声明定义了一种数据类型。在标志结构成员表结束的右花括号之后可以跟一个变 量表,这与其它基本类型的变量声明是相同的。例如:

   struct { ... } x, y, z;
   从语法角度来说,这种方式的声明与声明
   int x, y, z;

   具有类似的意义。这两个声明都将 x、y 与 z 声明为指定类型的变量,并且为它们分配存储空间。


   如果结构声明的后面不带变量表,则不需要为它分配存储空间,它仅仅描述了一个结构 的模板或轮廓。
   但是,如果结构声明中带有标记,那么在以后定义结构实例时便可以使用该 标记定义。
   例如,对于上面给出的结构声明 point,语句

   struct point pt;
   定义了一个 struct point 类型的变量 pt。结构的初始化可以在定义的后面使用初值表进
   行。初值表中同每个成员对应的初值必须是常量表达式,例如:
   struct point maxpt = {320, 200};

   在表达式中,可以通过下列形式引用某个特定结构中的成员:
   结构名.成员


   中的结构成员运算符“.”将结构名与成员名连接起来。例如,可用下列语句打印点 pt 的 坐标:
   printf("%d,%d", pt.x, pt.y);
*/



/* 2. 结构可以嵌套。

   struct rect {
        struct point pt1;
        struct point pt2;
   };

   结构 rect 包含两个 point 类型的成员。如果按照下列方式声明 screen 变量:
   struct rect screen;
   则可以用语句
   screen.pt1.x
   引用 screen 的成员 pt1 的 x 坐标。


   #include <stdio.h>

   struct point {
       int x;
       int y;
   } poi;

   struct rect {
       struct point po;
   } re;

   main() {
       struct point po = {10, 20};
       printf("x = %d, y = %d \n", po.x, po.y);

       poi.x = 5;
       poi.y = 25;
       printf("x = %d, y = %d \n", poi.x, poi.y);

       // struct rect re;
       // re.po.x = 3;
       // re.po.y = 6;
       re.po.x = 3;
       re.po.y = 5;
       printf("x = %d, y = %d \n", re.po.x, re.po.y);
       return 0;
   }
*/


/* 3. 结构与函数。

   结构的合法操作只有几种：作为一个整体复制与赋值，通过&运算符取地址，访问其成员。

   其中,复制和赋值包括向函数传递参数以及从函数返回值。
   结构之间不可以进行比较。

   可以 用一个常量成员值列表初始化结构,自动结构也可以通过赋值进行初始化。

   为了更进一步地理解结构,我们编写几个对点和矩形进行操作的函数。
   至少可以通过 3 种可能的方法传递结构:一是分别传递各个结构成员, 二是传递整个结构, 三是传递指向结 构的指针。
   这 3 种方法各有利弊。

   首先来看一下函数 makepoint,它带有两个整型参数,并返回一个 point 类型的结构:

   struct point makePoint(int x, int y) {
       struct point temp;
       temp.x = x;
       temp.y = y;

       return temp;
   }



   如果传递给函数的结构很大,使用指针方式的效率通常比复制整个结构的效率要高。结 构指针类似于普通变量指针。声明
   struct point *pp;

   将 pp 定义为一个指向 struct point 类型对象的指针。如果 pp 指向一个 point 结构,那
   么*pp即为该结构,而(*pp).x和(*pp).y则是结构成员。可以按照下例中的方式使用pp:

   // *pp指向结构的指针
   struct point origin, *pp;
   pp = &origin;
   printf("origin is (%d,%d)\n", (*pp).x, (*pp).y);




   结构指针的使用频度非常高,为了使用方便,C 语言提供了另一种简写方式。假定 p 是 一个指向结构的指针,可以用:
   p->结构成员
   printf("origin is (%d,%d)\n", pp->x, pp->y);


   运算符.和->都是从左至右结合的,所以,对于下面的声明:
   struct rect r, *rp = &r;

   r.pt1.x
   rp->pt1.x
   (r.pt1).x
   (rp->pt1).x
   都是合法的。




   在所有运算符中,下面 4 个运算符的优先级最高:结构运算符“.”和“->”、用于函数 调用的“()”
   以及用于下标的“[]”,因此,它们同操作数之间的结合也最紧密。例如,对于 结构声明：
   struct point {
        int len;
        char *str;
   } *p;

   ++p->len;//对len++，而不是p

   struct str *p = &str;
   p->len = 1;
   p->str = "hhh";
   ++p->len;
   printf("len = %d \n", p->len);

   将增加 len 的值,而不是增加 p 的值,这是田为,其中的隐含括号关系是++(p->len)。
   可 以使用括号改变结合次序。例如:(++p)->len 将先执行 p 的加 1 操作,再对 len 执行操作;
    而(p++)->len 则先对 len 执行操作,然后再将 p 加 1(该表达式中的括号可以省略)。

   同样的道理,*p->str 读取的是指针 str 所指向的对象的值;
   *p->str++先读取指针 str 指向的对象的值,然后再将 str 加 1(与*s++相同);
   (*p->str)++将指针 str 指向 的对象的值加 1;*p++->str 先读取指针 str 指向的对象的值,然后再将 p 加 1。
*/



/* 4. 结构数组。

   考虑编写这样一个程序,它用来统计输入中各个 C 语言关键字出现的次数。
   我们需要用 一个字符串数组存放关键字名,一个整型数组存放相应关键字的出现次数。
   一种实现方法是, 使用两个独立的数组 keyword 和 keycount 分别存放它们,如下所示
   char *keyword[NKEYS];
   int keycount[NKEYS];

   我们注意到,这两个数组的大小相同,考虑到该特点,可以采用另一种不同的组织方式,
   也就是我们这里所说的结构数组。每个关键字项包括一对变量:
   char *word;
   int count;

   struct key {
      char *word;
      int count;
   } keytab[NKEYS];

   也可以改成:
   struct key {
         char *word;
         int count;
      };
   struct key keytab[NKEYS];


   因为结构 keytab 包含一个固定的名字集合,所以,最好将它声明为外部变量,这样,
   只需要初始化一次,所有的地方都可以使用。这种结构的初始化方法同前面所述的初始化方法
   类似——在定义的后面通过一个用圆括号括起来的初值表进行初始化,如下所示:
   struct key {
         char *word;
         int count;
      } keytab[] = {
        {"auto", 0},
        {"break", 0},
        {"case", 0},
        {"char", 0},
        {"const", 0},
        {"continue", 0}
   };
*/



/* 5. sizeof
   C 语言提供了一个编译时(compile-time)一元运算符 sizeof,它可用来计算任一对象的长
   度。表达式: sizeof 对象，以及 sizeof(类型名)。
   #define NKEYS (sizeof keytab / sizeof(struct key))
   #define NKEYS (sizeof keytab / sizeof(keytab[0]))


   条件编译语句#if 中不能使用 sizeof,因为预处理器不对类型名进行分析。但预处理器
   并不计算#define 语句中的表达式,因此,在#define 中使用 sizeof 是合法的。
*/



/* 7. 类型定义(typedef)。

   C 语言提供了一个称为 typedef 的功能,它用来建立新的数据类型名,例如,声明
   typedef int Length;

   将 Length 定义为与 int 具有同等意义的名字。类型 Length 可用于类型声明、类型转换等,
   它和类型 int 完全相同,例如:

   就是别名。

   Length len, maxlen;
   Length *lengths[];

   类似的：
   typedef char* String;

   将 String 定义为与 char *或字符指针同义,此后,便可以在类型声明和类型转换中使用 String,例如:
   String p, lineptr[MAXLINES], alloc(int);
   int strcmp(String, String);
   p = (String) malloc(100);
*/

/* 8. 联合。

   联合是可以(在不同时刻)保存不同类型和长度的对象的变量,编译器负责跟踪对象的 长度和对齐要求。
   联合提供了一种方式,以在单块存储区中管理不同类型的数据,而不需要 在程序中嵌入任何同机器有关的信息。
   它类似于 Pascal 语言中的变体记录。

   我们来看一个例子(可以在编译器的符号表管理程序中找到该例子)。假设一个常量可能 是 int、f1oat 或字符指针。
   特定类型的常量值必须保存在合适类型的变量中,然而,如果 该常量的不同类型占据相同大小的存储空间,
   且保存在同一个地方的话,表管理将最方便。 这就是联合的目的——一个变量可以合法地保存多种数据类型中任何一种类型的对象。
   其语法基于结构,如下所示:
   union u_tag {
         int ival;
         float fval;
         char *sval;
   } u;



   变量 u 必须足够大,以保存这 3 种类型中最大的一种,具体长度同具体的实现有关。
   这些类型中的任何一种类型的对象都可赋值给u,且可使用在随后的表达式中,但必须保证是一致的
   :读取的类型必须是最近一次存入的类型。程序员负责跟踪当前保存在联合中的类型。
   如果保存的类型与读取的类型不一致,其结果取决于具体的实现。

   可以通过下列语法访问联合中的成员:
   联合名.成员 或
   联合指针->成员


   它与访问结构的方式相同。如果用变量 utype 跟踪保存在 u 中的当前数据类型,则可以像下
   面这样使用联合:
   if (utype == INT)
        printf("%d\n", u.ival);
   if (utype == FLOAT)
        printf("%f\n", u.fval);
   if (utype == STRING)
        printf("%s\n", u.sval);
   else
        printf("bad type %d in utype\n", utype);

   联合可以使用在结构和数组中,反之亦可。访问结构中的联合(或反之)的某一成员的 表示法与嵌套结构相同。
   例如,假定有下列的结构数组定义:
   struct {
         char *name;
         int flags;
         int utype;
         union {
            int ival;
            float fval;
            char *sval;
         } u;
      } symtab[NSYM];
   可以通过下列语句引用其成员 ival:
    symtab[i].u.ival

   也可以通过下列语句之一引用字符串 sval 的第一个字符:
   *symtab[i].u.sval
   symtab[i].u.sval[0]

   实际上,联合就是一个结构,它的所有成员相对于基地址的偏移量都为 0,此结构空间要 大到足够容纳最“宽”的成员,
   并且,其对齐方式要适合于联合中所有类型的成员。
   对联合允许的操作与对结构允许的操作相同:作为一个整体单元进行赋值、复制、取地址及访问其中一个成员。

   ＝＝＝＝ 联合只能用其第一个成员类型的值进行初始化,因此,上述联合 u 只能用整数值进行初 始化 ＝＝＝＝

   第 8 章的存储分配程序将说明如何使用联合来强制一个变量在特定类型的存储边界上对 齐。
*/

#include <stdio.h>
#include <ctype.h>
#include <string.h>

#define MAXWORD 100

struct point makePoint(int x, int y);
int getword(char *, int);
int binsearch(char *, struct key *, int);

struct point {
    int x;
    int y;
} poi;

struct rect {
    struct point po;
} re;

struct str {
    int len;
    char *str;
} str;

struct key {
         char *word;
         int count;
} keytab[] = {
        {"auto", 0},
        {"break", 0},
        {"case", 0},
        {"char", 0},
        {"const", 0},
        {"continue", 0}
   };

void testStruct() {
    struct point po = {10, 20};
    printf("x = %d, y = %d \n", po.x, po.y);

    poi.x = 5;
    poi.y = 25;
    printf("x = %d, y = %d \n", poi.x, poi.y);

    // struct rect re;
    // re.po.x = 3;
    // re.po.y = 6;
    re.po.x = 3;
    re.po.y = 5;
    printf("x = %d, y = %d \n", re.po.x, re.po.y);
}

void testStruct1() {
   struct point po = makePoint(10, 30);
    printf("x = %d, y = %d \n", po.x, po.y);

    // *pp指向结构的指针
    struct point origin, *pp;
    pp = &origin;
    // printf("origin is (%d,%d)\n", (*pp).x, (*pp).y);
    printf("origin is (%d,%d)\n", pp->x, pp->y);

    struct str *p = &str;
    p->len = 1;
    p->str = "hhh";
    ++p->len;
    printf("len = %d \n", p->len);
}

main() {
    // testStruct();
    // testStruct1();

    return 0;
}

struct point makePoint(int x, int y) {
    struct point temp;
    temp.x = x;
    temp.y = y;

    return temp;
}

/* binsearch: find word in tab[0]...tab[n-1] */
int binsearch(char *word, struct key tab[], int n) {
    int cond;
    int low, high, mid;
    low = 0;
    high = n - 1;
    while (low <= high) {
        mid = (low+high) / 2;
        if ((cond = strcmp(word, tab[mid].word)) < 0) {
            high = mid - 1;
        } else if (cond > 0) {
            low = mid + 1;
        } else {
            return mid;
        }
    }

    return -1;
}

/* getword: get next word or character from input */
int getword(char *word, int lim) {
    int c, getch(void);
    void ungetch(int);
    char *w = word;
    while (isspace(c = getch())) {
        ;
    }
    if (c != EOF) {
        *w++ = c;
    }
    if (!isalpha(c)) {
       *w = '\0';
       return c;
    }
    for ( ; --lim > 0; w++) {
        if (!isalnum(*w = getch())) {
            ungetch(*w);
            break;
        }
    }

    *w = '\0';
    return word[0];
}
