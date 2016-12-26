//
// Created by Mr.Kuang on 10/28/16.
// 第四章－管理数组和字符串
//


/* 1. long longArray[25];

   const static unsigned short int LEN = 5;

   void testArray1() {
       using namespace std;

       int myArray[LEN];
       int i = 0;

       for (; i < LEN; i++) {
           cout << "value for myArray[" << i << "] :";
           cin >> myArray[i];
       }

       for (i = 0; i < LEN; i++) {
           cout << i << ": " << myArray[i] << endl;
       }
   }
*/


/* 2. 字符数组和字符串。

   也提供像c那样的字符数组，以\0结尾。

   但是有更方便的写法：

   char greeting[] = "greeting";
   字符数组的长度总是所见的长度 + 1，一个空字符。

   char test[] = "hello";
   std :: cout << test << std :: endl;
*/


/* 3. strcpy, strncpy

   strcpy 将一个字符串复制到另一个字符串中
   strncpy 将指定数目的字符串复制到另一个字符串


   char string1[] = "No man is island";
   char string2[80] = {'\0'};

   strcpy(string2, string1);

   cout << "string2 : " << string2 << endl;

   char string3[80] = {'\0'};

   strncpy(string3, string2, 3);

   cout << "string3 : " << string3 << endl;

   长度得加上 \0 空字符。
*/


/* 4. string 类。

   c++ 标准库提供了一个string类，它提供了封装的数据集和操作这些字符串的函数，使得处理字符串变得轻松。
   std :: string 负责处理内存分配。
   这使得复制字符串和给它们赋值变得很简单。

   std :: string str1 ("this is a c++ string");
   cout << "str1 : " << str1 << endl;

   string str2 = str1;
   cout << "str2 : " << str2 << endl;

   string result = str1 + str2;
   cout << "result : " << result << endl;
*/



#include <iostream>
#include <string.h>

using namespace std;

const static unsigned short int LEN = 5;

void testArray1();
void testArray2();
void testArray3();
void testCharArray();
void testString();

int main() {
    // testArray1();
    // testArray2();
    // testArray3();
    // testCharArray();
    testString();

    return 0;
}

void testArray1() {
    using namespace std;

    int myArray[LEN];
    int i = 0;

    for (; i < LEN; i++) {
        cout << "value for myArray[" << i << "] :";
        cin >> myArray[i];
    }

    for (i = 0; i < LEN; i++) {
        cout << i << ": " << myArray[i] << endl;
    }
}

void testArray2() {
    int testArray[] = {1, 2, 3, 4, 5};
    std :: cout << sizeof (testArray) / sizeof (*testArray) << std :: endl;
}

void testArray3() {
    char test[] = "hello";
    std :: cout << test << std :: endl;
}

void testCharArray() {
    char string1[] = "No man is island";
    char string2[80] = {'\0'};

    strcpy(string2, string1);

    cout << "string2 : " << string2 << endl;

    char string3[80] = {'\0'};

    strncpy(string3, string2, 3);

    cout << "string3 : " << string3 << endl;
}

void testString() {
    std :: string str1 ("this is a c++ string");
    cout << "str1 : " << str1 << endl;

    string str2 = str1;
    cout << "str2 : " << str2 << endl;

    string result = str1 + str2;
    cout << "result : " << result << endl;
}