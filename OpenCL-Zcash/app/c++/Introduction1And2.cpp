//
// Created by Mr.Kuang on 10/28/16.
// 第一章－绪论
// 第二章－c++组成部分
//


/* 1.c++在c的基础上，增加了面向对象，多态，继承等。

   解释器和编译器：

   解释器解释翻译并执行程序，它读取程序源代码，并直接将其转换成操作。编译器先将源代码转换成中间格式，这
   通常被称为编译，编译生成目标文件。然后编译器调用链接器，将目标文件组合成为可执行程序。其中包括可以直接
   在处理器上执行的机器代码。

   c++包含面向对象的三大要素：继承，封装，多态。

   编写程序，编译源代码，链接程序和运行。


   #include <iostream>

   int main() {
       std::cout << "hello world \n";
       return 0;
   }

  <<是重定向符，

   编译运行：
   g++ Introduction1.cpp
   ./a.out
*/



/* 2. 文件io被包含到当前文件中，#是预处理器标记，每次编译器启动时，都首先会运行预处理器。
   预处理器会浏览源代码，查找以#开头的行，在编译器运行前处理这些行。
   include是一条预处理指令，指出接下来是一个文件名，请找到该文件，读取它并将它放在这里。
   文件名两边的尖括号告诉预处理器，在通常的所有位置查找该文件，如果编译器安装正确，尖括号
   将使预处理器在保存编译器的所有包含文件的目录下查找iostream文件。

   注意，并非所有的编译器都支持在#include语句中中省略文件扩展名，如果出现错误信息，则可能要修改
   编译器包含的搜索路径或者在#include中加上文件的扩展名。

   cout,cin分别来处理输出和输入。

   std::cout << "hello world \n";
   // char response;
   // std::cin >> response;
   std::cout << "Here is 5: " << 5 << "\n";
   std::cout << "The manipulator std :: end1 ";
   std::cout << "writes a new line to the screen. ";
   // std :: endl表示换行并且刷新输出流
   std::cout << std :: endl;
   std::cout << "Here is a very big number:\t" << 70000;
   std::cout << std :: endl;
   std::cout << "Here is the sum 8 and 5:\t";
   std::cout << 8 + 5 << std :: endl;
   std::cout << "Here's a fraction:\t\t";
   std::cout << (float) 5 / 8 << std :: endl;
   std::cout << "And a very big number: \t";
   std::cout << (double) 7000 * 7000 << std :: endl;
*/



/* 3. 使用标准名称空间。
   您将发现，在cout和cin前面加上std很烦人，尽管使用名称空间指示是一种很好的的方式，但是大量的输入
   很讨厌，有两种方式可以解决这个问题。

   第一种方式是在代码清单的开头告诉编译器，您将使用标准库cout, endl。
   using std::cout;
   using std::endl;

   cout << "hello world \n";

   void outPrint1() {
       using std::cout;
       using std::endl;

       cout << "Hello world" << endl;
   }


   第二种解决方式是，告诉编译器程序将使用整个名称空间，也就是说，除非特别声明，否则任何对象都来自
   标准名称空间。在这种情况下，应使用 using namespace std而不是using std :: cout;

   void outPrint2() {
       using namespace std;

       cout << "Hello world" << endl;
   }
*/



/* 4. 函数。

   int add(int first, int second) {
       using namespace std;

       cout << "add() received " << first << " and " << second << endl;

       return first + second;
   }
*/

#include <iostream>

void outPrint();
void outPrint1();
void outPrint2();
int add(int, int);

int main() {
    // outPrint();
    // outPrint1();
    // outPrint2();
    std::cout << add(3, 5) << std :: endl;

    return 0;
}

void outPrint() {
   std::cout << "hello world \n";
   // char response;
   // std::cin >> response;
   // 这一行是将三个值传给cout，插入运算符
   std::cout << "Here is 5: " << 5 << "\n";
   std::cout << "The manipulator std :: end1 ";
   std::cout << "writes a new line to the screen. ";
   // std :: endl表示换行并且刷新输出流，end line
   // endl比\n要好，因为endl将适应当前的操作系统，而有些os或平台上，\n可能不是完整的换行符
   std::cout << std :: endl;
   std::cout << "Here is a very big number:\t" << 70000;
   std::cout << std :: endl;
   std::cout << "Here is the sum 8 and 5:\t";
   std::cout << 8 + 5 << std :: endl;
   std::cout << "Here's a fraction:\t\t";
   std::cout << (float) 5 / 8 << std :: endl;
   std::cout << "And a very big number: \t";
   std::cout << (double) 7000 * 7000 << std :: endl;
}

void outPrint1() {
    using std::cout;
    using std::endl;

    cout << "Hello world" << endl;
}

void outPrint2() {
    using namespace std;

    cout << "Hello world" << endl;
}

int add(int first, int second) {
    using namespace std;

    cout << "add() received " << first << " and " << second << endl;

    return first + second;
}