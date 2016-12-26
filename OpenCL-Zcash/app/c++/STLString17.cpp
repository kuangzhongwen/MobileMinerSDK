//
// Created by Mr.Kuang on 11/2/16.
// 第十七章－STL String类
//


/* 1.String类不仅能够根据应用程序的需求动态调整其大小，还提供了很有用的助手函数可帮助操作字符串。

   在本章中，您将学习：

   －为何需要字符串操作类
   －使用STL String类
   －基于模版的STL String类的实现
*/


/* 2.使用STL String类。

   最常见的字符串函数包括：
   －复制
   －连接
   －查找字符和子字符串
   －截短
   －使用标准模版库提供的算法实现字符串反转和大小写转换
*/


/* 3.实例化STL String及复制。

   string提供了很多重载的构造函数，因此可以多种方式进行实例化和初始化。例如，初始化一个常量
   字符串并将其赋值给一个STL String对象。

   const char *pszFromConst = "hello string";
   std :: string strFromConst (pszFromConst);



#include <iostream>

using namespace std;

void testString() {
    const char *pszFromConst = "hello string";
    std :: string strFromConst (pszFromConst);
    cout << strFromConst << endl;

    // 这种方式和上面的其实是一样的
    string str2 ("ni hao");
    cout << str2 << endl;

    string str3 (str2);
    cout << str3 << endl;

    // 只复制部分
    string strPart (str2, 2);
    cout << strPart << endl;

    string strReplace (10, 'n');
    cout << strReplace << endl;
}

int main() {
    testString();

    return 0;
}


hello string
ni hao
ni hao
 hao
nnnnnnnnnn

*/



/* 3.访问string及其内容。

   －访问STL String的字符元素

       std :: string str ("halou o");
       for (int i = 0; i < str.length(); i++) {
           cout << "character[" << i << "]:";
           cout << str[i] << endl;
       }


    out:
    character[0]:h
    character[1]:a
    character[2]:l
    character[3]:o
    character[4]:u
    character[5]:
    character[6]:o





    // 利用迭代器访问字符串的字符元素
        std :: string str ("cao ni da ye");
        int charOffset = 0;
        string :: const_iterator icharIter;
        for (icharIter = str.begin(); icharIter != str.end(); icharIter++) {
            cout << *icharIter << endl;
        }


    c
    a
    o

    n
    i

    d
    a

    y
    e
*/




/* 4.字符串连接。

   要连接字符串，可以用运算符 += ，也可以用成员函数append。

   void testStringCotact() {
       string sample1 = "Hello";
       string sample2 = "string";
       sample1 += sample2;

       cout << "sample1:" << sample1 << endl;

       string sample3 = " caca";
       sample1.append(sample3);
       cout << "sample1:" << sample1 << endl;

       const char* pszhString = " you however still can!";
       sample1.append(pszhString);
       cout << "sample1:" << sample1 << endl;
   }


   out:

   sample1:Hellostring
   sample1:Hellostring caca
   sample1:Hellostring caca you however still can!
*/



/* 5.在string中查找字符或子字符。

   STL string提供的find函数。


    string sample = "wo shi kuang";
    int offset = sample.find("kuang", 0);
    if (offset != string :: npos) {
        cout << "find kuang:" << offset << endl;
    } else {
        cout << "no found kuang" << endl;
    }




    void testStringFind() {
        string sample = "wo shi kuang a li";
        int offset = sample.find("kuang", 0);
        if (offset != string :: npos) {
            cout << "find kuang:" << offset << endl;
        } else {
            cout << "no found kuang" << endl;
        }

        const char searchChar = 'a';
        int charOffset = sample.find(searchChar, 0);
        while (charOffset != string :: npos) {
            cout << "'" << searchChar << "' found";
            cout << " at position: " << charOffset << endl;
            charOffset++;
            charOffset = sample.find(searchChar, charOffset);
        }
    }


    out :

    find kuang:7
    'a' found at position: 9
    'a' found at position: 13
*/



/* 6.截短STL string

   STL string提供了erase函数，可用于：
   －在给定偏移位置和字符数时删除指定数目的字符。
   －在指定字符的迭代器时删除字符。
   －在给定由两个迭代器指定的范围时删除该范围内的字符。

       string strSample = "Hello String! wake up to a beautiful day!";
       cout << "The original sample string is : " << endl << endl;
       cout << "Truncating the second sentence : " << endl;
       strSample.erase(13, 28);
       cout << strSample << endl << endl;

       // find a char 'S' in the string using STL find algorithm
       string :: iterator iChars = find (strSample.begin(), strSample.end(), 'S');
       // if char found, 'erase' to deletes a char
       if (iChars != strSample.end()) {
           strSample.erase(iChars);
       }
       cout << strSample << endl << endl;

       cout << "Erasing a range between begin() and end() : " << endl;
       strSample.erase(strSample.begin(), strSample.end());
       if (strSample.length() == 0) {
           cout << "The string is empty" << endl;
       }

    out :
    The original sample string is :

    Truncating the second sentence :
    Hello String!

    Hello tring!

    Erasing a range between begin() and end() :
    The string is empty
*/



/* 7. 字符串反转。


    string strSample = "Hello String, we will revert you!";
    cout << "The original sample string is : " << endl << endl;
    cout << strSample << endl;

    reverse(strSample.begin(), strSample.end());

    cout << "After applying the std :: reverse algorithm : " << endl;
    cout << strSample << endl;


    out :
    The original sample string is :

    Hello String, we will revert you!
    After applying the std :: reverse algorithm :
    !uoy trever lliw ew ,gnirtS olleH
    kuangzhongwendeMacBook-Pro:c++ kuangz
*/


/* 8. 字符串的大小写转换。
*/



#include <iostream>
#include <string>
#include <algorithm>

using namespace std;

void testString() {
    const char *pszFromConst = "hello string";
    std :: string strFromConst (pszFromConst);
    cout << strFromConst << endl;

    // 这种方式和上面的其实是一样的
    string str2 ("ni hao");
    cout << str2 << endl;

    string str3 (str2);
    cout << str3 << endl;

    // 只复制部分
    string strPart (str2, 2);
    cout << strPart << endl;

    string strReplace (10, 'n');
    cout << strReplace << endl;
}

void testString1() {
    std :: string str ("halou o");
    for (int i = 0; i < str.length(); i++) {
        cout << "character[" << i << "]:";
        cout << str[i] << endl;
    }
}

void testString2() {
    // 利用迭代器访问字符串的字符元素
    std :: string str ("cao ni da ye");
    int charOffset = 0;
    string :: const_iterator icharIter;
    for (icharIter = str.begin(); icharIter != str.end(); icharIter++) {
        cout << *icharIter << endl;
    }
}

void testStringCotact() {
    string sample1 = "Hello";
    string sample2 = "string";
    sample1 += sample2;

    cout << "sample1:" << sample1 << endl;

    string sample3 = " caca";
    sample1.append(sample3);
    cout << "sample1:" << sample1 << endl;

    const char* pszhString = " you however still can!";
    sample1.append(pszhString);
    cout << "sample1:" << sample1 << endl;
}

void testStringFind() {
    string sample = "wo shi kuang a li";
    int offset = sample.find("kuang", 0);
    if (offset != string :: npos) {
        cout << "find kuang:" << offset << endl;
    } else {
        cout << "no found kuang" << endl;
    }

    const char searchChar = 'a';
    int charOffset = sample.find(searchChar, 0);
    while (charOffset != string :: npos) {
        cout << "'" << searchChar << "' found";
        cout << " at position: " << charOffset << endl;
        charOffset++;
        charOffset = sample.find(searchChar, charOffset);
    }
}

void testStringSub() {
    string strSample = "Hello String! wake up to a beautiful day!";
    cout << "The original sample string is : " << endl << endl;
    cout << "Truncating the second sentence : " << endl;
    strSample.erase(13, 28);
    cout << strSample << endl << endl;

    // find a char 'S' in the string using STL find algorithm
    string :: iterator iChars = find (strSample.begin(), strSample.end(), 'S');
    // if char found, 'erase' to deletes a char
    if (iChars != strSample.end()) {
        strSample.erase(iChars);
    }
    cout << strSample << endl << endl;

    cout << "Erasing a range between begin() and end() : " << endl;
    strSample.erase(strSample.begin(), strSample.end());
    if (strSample.length() == 0) {
        cout << "The string is empty" << endl;
    }
}

void testStringReverse() {
    string strSample = "Hello String, we will revert you!";
    cout << "The original sample string is : " << endl << endl;
    cout << strSample << endl;

    reverse(strSample.begin(), strSample.end());

    cout << "After applying the std :: reverse algorithm : " << endl;
    cout << strSample << endl;
}

void testStringUpperLower() {
    cout << "Please enter a string for case-conversion : " << end;
    cout << "> ";
    string strInput;

    getline(cin, strInput);
    cout << endl;

    transform(strInput.begin(), strInput.end(), strInput.begin(), toupper);
    cout << "The string converted to upper case is : " << endl;
    cout << strInput << endl << endl;

    transform(strInput.begin(), strInput.end(), strInput.begin(), tolower);
    cout << "The string converted to lower case is : " << endl;
    cout << strInput << endl << endl;
}

int main() {
    testStringUpperLower();

    return 0;
}