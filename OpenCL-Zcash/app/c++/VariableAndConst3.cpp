//
// Created by Mr.Kuang on 10/28/16.
// 第三章－使用变量和常量
//


/* 1. 在c++定义变量时，必须告诉编译器变量的类型：整数，浮点数，字符型。
   整型变量的大小，整数在一体啊机器上可能是2个字节，也可能是4个字节。
   单个字符，通常是一个字节。
   对于较小的整数，可以使用 short 来存储，在大多数计算机上，short 为2个字节，
   long 为4个字节，而通常int 为2个或4个字节。

   signed 和 unsigned，有时要求整型能存储正数，负数。
   没有使用 unsigned 声明的整型变量都被视为无符号，这种变量可以正，也可以负。
   但是使用了 unsigned 声明的整型变量，就只能为正。

   signed 和 unsigned 整型变量占用的内存空间相同，但 signed 整型变量的部分存储空间被用于
   存储指出该变量是正还是负的信息。因此 unsigned 整型变量能够存储的最大值为signed的整型变量
   能存储的最大正数的2倍。

   如果short 变量占2个字节，则 unsigned short 取值范围为 0 ~ 65535, 而 signed short 的取值范围为
   -32768 ~ 32767

   unsigned short x = 10;
   unsigned short y = 11;
   unsigned short z = x + y;

   using namespace std;
   cout << z << endl;
*/


/* 2. 确定变量类型占用的内存量。

   using namespace std;

   cout << "The size of int : " << sizeof(int) << " bytes" << endl;
   cout << "The size of short int : " << sizeof(short) << " bytes" << endl;
   cout << "The size of long int : " << sizeof(long) << " bytes" << endl;
   cout << "The size of char int : " << sizeof(char) << " bytes" << endl;
   cout << "The size of float int : " << sizeof(float) << " bytes" << endl;
   cout << "The size of double int : " << sizeof(double) << " bytes" << endl;
   cout << "The size of bool int : " << sizeof(bool) << " bytes" << endl;

   The size of int : 4 bytes
   The size of short int : 2 bytes
   The size of long int : 8 bytes
   The size of char int : 1 bytes
   The size of float int : 4 bytes
   The size of double int : 8 bytes
   The size of bool int : 1 bytes
*/


/* 3. typedef
   给数据类型起别名。

   typedef unsigned short int USHORT;


   USHORT width = 0;
   USHORT height = 0;

   using namespace std;

   cout << width << endl << height << endl;
*/

/* 4. char

   using namespace std;

   for (int i = 32; i < 128; i++) {
       cout << (char) i << endl;
   }
*/


/* 5. 常量

   - 使用#define定义常量。

   #define studentPerClass 15

   - 使用const定义常量

   const long int per = 12;
   const USHORT uper = 3;
*/



/* 6. 枚举常量

   enum COLOR {RED, BLUE, WHITE, GREEN, BLACK};
   enum DAY {SUNDAY = 100, MONDAY = 200, TUESDAY = 300};

   DAY day = SUNDAY;
   if (day == SUNDAY) {
      std :: cout << "Today is sunday;" << std :: endl;
   }
*/





#include <iostream>

#define studentPerClass 15

typedef unsigned short int USHORT;

const long int per = 12;
const USHORT uper = 3;

void variable();
void sizeofTest();
void typedefTest();
void charTest();
void enumTest();

enum COLOR {RED, BLUE, WHITE, GREEN, BLACK};
enum DAY {SUNDAY = 100, MONDAY = 200, TUESDAY = 300};

int main() {
    // variable();
    // sizeofTest();
    // typedefTest();
    // charTest();
    enumTest();

    return 0;
}

void variable() {
    unsigned short x = 10;
    unsigned short y = 11;
    unsigned short z = x + y;

    using namespace std;
    cout << z << endl;
}

void sizeofTest() {
    using namespace std;

    cout << "The size of int : " << sizeof(int) << " bytes" << endl;
    cout << "The size of short int : " << sizeof(short) << " bytes" << endl;
    cout << "The size of long int : " << sizeof(long) << " bytes" << endl;
    cout << "The size of char int : " << sizeof(char) << " bytes" << endl;
    cout << "The size of float int : " << sizeof(float) << " bytes" << endl;
    cout << "The size of double int : " << sizeof(double) << " bytes" << endl;
    cout << "The size of bool int : " << sizeof(bool) << " bytes" << endl;

    /**
    The size of int : 4 bytes
    The size of short int : 2 bytes
    The size of long int : 8 bytes
    The size of char int : 1 bytes
    The size of float int : 4 bytes
    The size of double int : 8 bytes
    The size of bool int : 1 bytes
    */
}

void typedefTest() {
    USHORT width = 0;
    USHORT height = 0;

    using namespace std;

    cout << width << endl << height << endl;
}

void charTest() {
    using namespace std;

    for (int i = 32; i < 128; i++) {
        cout << (char) i << endl;
    }
}

void enumTest() {
    DAY day = SUNDAY;
    if (day == SUNDAY) {
        std :: cout << "Today is sunday;" << std :: endl;
    }
}