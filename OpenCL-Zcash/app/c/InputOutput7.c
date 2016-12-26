//
// Created by Mr.Kuang on 10/27/16.
// 第七章－输入与输出
//

/* 1. 输入/输出功能并不是 C 语言本身的组成部分,所以到目前为止,我们并没有过多地强 调它们。
      但是,程序与环境之间的交互比我们在前面部分中描述的情况要复杂很多,本章将 讲述标准库,介绍一些输入/输出函数
      、字符串处理函数、存储管理函数与数学函数,以及 其它一些 C 语言程序的功能。本章讨论的重点将放在输入/输出上。
      ANSI 标准精确地定义了这些库函数,所以,在任何可以使用 C 语言的系统中都有这些函 数的兼容形式。
      如果程序的系统交互部分仅仅使用了标准库提供的功能,则可以不经修改地 从一个系统移植到另一个系统中。
      这些库函数的属性分别在十多个头文件中声明,前面已经遇到过一部分,如<stdio.h>、 <string.h>和<ctype.h>。
      我们不打算把整个标准库都罗列于此,因为我们更关心如何使 用标准库编写 C 语言程序。附录 B 对标准库进行了详细的描述。
*/


/* 2. 标准输入/输出：
      int getchar(void)
      int putchar(int)

      格式化输出——printf 函数：
      int printf(char *format, arg1, arg2, ...);

      表7-1 printf函数基本的转换说明
      字符 参数类型:输出形式 int 类型;十进制数
      int 类型;无符号八进制数(没有前导 0)
      int 类型;无符号十六进制数(没有前导 0x 或 0X),10~15 分别用 abcdef 或 ABCDEF 表示
      int 类型;无符号十进制数
      int 类型;单个字符
      char *类型;顺序打印字符串中的字符,直到遇到'\0'或已打印了由精度指定的字符数为止
      double 类型;十进制小数[-]m.dddddd,其中 d 的个数由精度指定(默认值为 6)
      double 类型;[-]m.dddddd e ±xx 或[-]m.dddddd E ±xx,其中 d 的个数由精度指定(默认值为 6)
      double 类型;如果指数小于-4 或大于等于精度,则用%e 或%E 格式输出,否则用%f 格式输出。尾部的 0 和 小数点不打印
      void *类型;指针(取决于具体实现)
      不转换参数;打印一个百分号%



      变长参数表：
      void minprintf(char *fmt, ...)



      格式化输入——scanf 函数：
      int scanf(char *format, ...)
      int sscanf(char *string, char *format, arg1, arg2, ...)
*/




/* 3. 文件访问。

      该指针称为文件指针,它指向一个包含文件信息的结构,这些信息包括:缓冲区的位置、
      缓冲区中当前字符的位置、文件的读或写状态、是否出错或是否已经到达文件结尾等等。
      用户不必关心这些细节,因为<stdio.h>中已经定义了一个包含这些信息的结构 FILE。
      在程序中只需按照下列方式声明一个文件指针即可:

      FILE *fp;
      FILE *fopen(char *name, char *mode);


      fp 是一个指向结构 FILE 的指针,并且,fopen 函数返回一个指向结构 FILE 的 指针。

      注意,FILE 像 int 一样是一个类型名,而不是结构标记。它是通过 typedef 定义的 (UNIX 系统中
      fopen 的实现细节将在 8.5 节中讨论)。

      在程序中,可以这样调用 fopen 函数:
      fp = fopen(name, mode);
      fopen 的第一个参数是一个字符串,它包含文件名。
      第二个参数是访问模式,也是一个字符 串,用于指定文件的使用方式。
      允许的模式包括:读(“r”)、写(“w”)及追加(“a”)。
      某些系统还区分文本文件和二进制文件,对后者的访问需要在模式字符串中增加字符“b”。


      如果打开一个不存在的文件用于写或追加,该文件将被创建(如果可能的话)。
      当以写方式打开一个已存在的文件时,该文件原来的内容将被覆盖。
      但是,如果以追加方式打开一个 文件,则该文件原来的内容将保留不变。
      读一个不存在的文件会导致错误,其它一些操作也 可能导致错误,比如试图读取一个无读取权限的文件。
      如果发生错误,fopen 将返回 NULL。



      文件被打开后,就需要考虑采用哪种方法对文件进行读写。有多种方法可供考虑,其中, getc 和 putc 函数最为简单。
      getc 从文件中返回下一个字符,它需要知道文件指针,以确 定对哪个文件执行操作:

      int getc(FILE *fp)

      getc 函数返回 fp 指向的输入流中的下一个字符。如果到达文件尾或出现错误,该函数将返
      回 EOF,


      putc 是一个输出函数,如下所示:
      int putc(int c, FILE *fp)
      该函数将字符 c 写入到 fp 指向的文件中,并返回写入的字符。如果发生错误,则返回 EOF。


      类似于 getchar 和 putchar,getc 和 putc 是宏而不是函数。
*/



/* 4.启动一个 C 语言程序时,操作系统环境负责打开 3 个文件,并将这 3 个文件的指针提供 给该程序。
    这 3 个文件分别是标准输入、标准输出和标准错误。

    相应的文件指针分别为 stdin、 stdout 和 stderr,它们在<stdio.h>中声明。

    在大多数环境中,stdin 指向键盘,而 stdout 和 stderr 指向显示器。

    getchar 和 putchar 函数可以通过 getc、putc、stdin 及 stdout 定义如下:
    #define getchar() getc(stdin)
    #define putchar(c) putc((c),stdout)




    对于文件的格式化输入或输出,可以使用函数 fscanf 和 fprintf。它们与 scanf 和 printf
    函数的区别仅仅在于它们的第一个参数是一个指向所要读写的文件的指针,第二个参 数是格式串。如下所示：


    int fscanf(FILE *fp, char *format, ...)
    int fprintf(FILE *fp, char *format, ...)

    #include <stdio.h>

    void filecopy(FILE *, FILE *);

    main(int argc, char *argv[]) {
        FILE *fp;

        if (argc == 1) {
            filecopy(stdin, stdout);
        } else {
            while (--argv > 0) {
                if ((fp = fopen(*++argv, "r")) == NULL) {
                    printf("cat: can't open %s\n", *argv);
                    return 1;
                } else {
                    filecopy(fp, stdout);
                    fclose(fp);
                }
            }
        }

        return 0;
    }

    void filecopy(FILE *ifp, FILE *ofp) {
        int c;
        while ((c = getc(ifp)) != EOF) {
            putc(c, ofp);
        }
    }

    -----文件指针stdin与stdout都是FILE *类型的对象。但它们是常量,而非变量。因此不能对它们赋值。

    函数
    int fclose(FILE *fp)
*/



/*  5. 错误处理——stderr 和 exit。

    if (ferror(stdout)) {
        fprintf(stderr, "%s: error writing stdout\n", prog);
        exit(2);
    }
    exit(0);
*/


/* 6. 行输入和行输出。

   标准库提供了一个输入函数 fgets,它和前面几章中用到的函数 getline 类似。
   char *fgets(char *line, int maxline, FILE *fp)

   fgets 函数从 fp 指向的文件中读取下一个输入行(包括换行符),并将它存放在字符数组
   line 中,它最多可读取 maxline-1 个字符。读取的行将以'\0'结尾保存到数组中。
   通常情 况下,fgets 返回 line,但如果遇到了文件结尾或发生了错误,则返回 NULL(
   我们编写的 getline 函数返回行的长度,这个值更有用,当它为 0 时意味着已经到达了文件的结尾)。


   输出函数 fputs 将一个字符串(不需要包含换行符)写入到一个文件中:
   int fputs(char *line, FILE *fp) 如果发生错误,该函数将返回 EOF,否则返回一个非负值。


   库函数 gets 和 puts 的功能与 fgets 和 fputs 函数类似,但它们是对 stdin 和 stdout 进行操作。有一点我们需要注意,gets 函数在读取字符串时将删除结尾的换行符('\n'), 而 puts 函数在写入字符串时将在结尾添加一个换行符。
   下面的代码是标准库中 fgets 和 fputs 函数的代码,从中可以看出,这两个函数并没 有什么特别的地方。代码如下所示:
   char *fgets(char *s, int n, FILE *iop)
   {
   register int c; register char *cs;
   cs = s;
   while (--n > 0 && (c = getc(iop)) != EOF)
   if ((*cs++ = c) == '\n') break;
   *cs = '\0';
   return (c == EOF && cs == s) ? NULL : s; }
   int fputs(char *s, FILE *iop)
   {
   int c;
         while (c = *s++)
            putc(c, iop);
   return ferror(iop) ? EOF : 0; }
*/


/* 7. 字符串操作函数。

      前面已经提到过字符串函数 strlen、strcpy、strcat 和 strcmp,它们都在头文件 <string.h>中定义。
      在下面的各个函数中,s 与 t 为 char *类型,c 与 n 为 int 类型。
      strcat(s, t) 将 t 指向的字符串连接到 s 指向的字符串的末尾
      strncat(s,t,n) 将t指向的字符串中前n个字符连接到s指向的字符串的末尾
      strcmp(s, t) 根据 s 指向的字符串小于(s<t)、等于(s==t)或大于(s>t)t 指向的字符串的不同情况,分别返回负整数、0 或正整数
      strncmp(s,t,n) 同strcmp相同,但只在前n个字符中比较
      strcpy(s, t) 将 t 指向的字符串复制到 s 指向的位置
      strncpy(s,t,n) 将t指向的字符串中前n个字符复制到s指向的位置
      strlen(s) 返回 s 指向的字符串的长度
      strchr(s, c) 在s指向的字符串中查找c,若找到,则返回指向它第一次出现的位 置的指针,否则返回 NULL
      strrchr(s, c) 在s指向的字符串中查找c,若找到,则返回指向它最后一次出现的 位置的指针,否则返回 NULL
*/


/* 8. 字符类别测试和转换函数。

      头文件<ctype.h>中定义了一些用于字符测试和转换的函数。在下面各个函数中,c 是
      一个可表示为unsigned char类型或EOF的int对象。该函数的返回值类型为int。

      isalpha(c) 若 c 是字母,则返回一个非 0 值,否则返回 0
      isupper(c)  若 c 是大写字母,则返回一个非 0 值,否则返回 0
      islower(c) 若 c 是小写字母,则返回一个非 0 值,否则返回 0
      isdigit(c) 若 c 是数字,则返回一个非 0 值,否则返回 0
      isalnum(c) 若 isalpha(c)或 isdigit(c),则返回一个非 0 值,否则返回 0
      isspace(c) 若 c 是空格、横向制表符、换行符、回车符,换页符或纵向制表符, 则返回一个非 0 值
      toupper(c) 返回 c 的大写形式
      tolower(c) 返回 c 的小写形式
*/


/* 9. 命令执行函数。

      函数 system(char* s)执行包含在字符申 s 中的命令,然后继续执行当前程序。
      s 的 内容在很大程度上与所用的操作系统有关。下面来看一个 UNIX 操作系统环境的小例子。语 句
       system("date");
      将执行程序 date,它在标准输出上打印当天的日期和时间。
      system 函数返回一个整型的状 态值,其值来自于执行的命令,并同具体系统有关。
      在 UNIX 系统中,返回的状态是 exit 的 返回值。
*/


/* 10. 存储管理函数。

       函数 malloc 和 calloc 用于动态地分配存储块。函数 malloc 的声明如下:
       void *malloc(size_t n)

       当分配成功时,它返回一个指针,设指针指向 n 字节长度的未初始化的存储空间,否则返回
       NULL。

       函数 calloc 的声明为
       void *calloc(size_t n, size_t size)


       当分配成功时,它返回一个指针,该指针指向的空闲空间足以容纳由 n 个指定长度的对象组 成的数组,
       否则返回 NULL。该存储空间被初始化为 0。

       根据请求的对象类型,malloc或calloc函数返回的指针满足正确的对齐要求。下面的 例子进行了类型转换:
       int *ip;
       ip = (int *) calloc(n, sizeof(int));


       free(p)函数释放 p 指向的存储空间,其中,p 是此前通过调用 malloc 或 calloc 函数得到的指针。
       存储空间的释放顺序没有什么限制,但是,如果释放一个不是通过调用 malloc 或 calloc
       函数得到的指针所指向的存储空间,将是一个很严重的错误。
*/


/* 11. 数学函数。

       头文件<math.h>中声明了 20 多个数学函数。下面介绍一些常用的数学函数,每个函数
       带有一个或两个 double 类型的参数,并返回一个 double 类型的值。

       sin(x)  x 的正弦函数,其中 x 用弧度表示
       cos(x)  x 的余弦函数,其中 x 用弧度表示
       atan2(y, x) y/x 的反正切函数,其中,x 和 y 用弧度表示
       exp(x) 指数函数 ex
       log(x) x 的自然对数(以 e 为底),其中,x>0
       log10(x) x 的常用对数(以 10 为底),其中,x>0
       pow(x, y) 计算xy的值
       sqrt(x) x 的平方根(x≥0)
       fabs(x) x 的绝对值
*/


/* 12. 随机数发生器函数。

       函数 rand()生成介于 0 和 RAND_MAX 之间的伪随机整数序列。
       其中 RAND_MAX 是在头 文件<stdlib.h>中定义的符号常量。
       下面是一种生成大于等于 0 但小于 1 的随机浮点数的 方法:

       #define frand() ((double) rand() / (RAND_MAX + 1.0))


       (如果所用的函数库中已经提供了一个生成浮点随机数的函数,那么它可能比上面这个函数 具有更好的统计学特性。)
       函数 srand(unsigned)设置 rand 函数的种子数。我们在 2.7 节中给出了遵循标准的 rand 和
       srand 函数的可移植的实现。
*/



#include <stdio.h>

void filecopy(FILE *, FILE *);

/* cat: concatenate files, version 1 */
main(int argc, char *argv[]) {
    FILE *fp;

    if (argc == 1) {
        filecopy(stdin, stdout);
    } else {
        while (--argv > 0) {
            if ((fp = fopen(*++argv, "r")) == NULL) {
                printf("cat: can't open %s\n", *argv);
                return 1;
            } else {
                filecopy(fp, stdout);
                fclose(fp);
            }
        }
    }

    return 0;
}

/* filecopy: copy file ifp to file ofp */
void filecopy(FILE *ifp, FILE *ofp) {
    int c;/,fp)) != EOF) {
        putc(c, ofp);
    }
}