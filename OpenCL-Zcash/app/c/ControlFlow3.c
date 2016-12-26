//
// Created by Mr.Kuang on 10/26/16.
// 第三章－控制流
//

/*
   1.if-else
   2.else-if
   3.switch
   4.while
   5.for
   6.do-while
   7.break, continue
   8.goto:
     c语言提供了可随意滥用的goto语句以及标记跳转位置的标号，从理论上讲,goto 语句是没有必要的,
     实践中不使用 goto 语句也可以很容易地写出代码。

     但是,在某些场合下 goto 语句还是用得着的。最常见的用法是终止程序在某些深度嵌套
     的结构中的处理过程,例如一次跳出两层或多层循环。这种情况下使用 break 语句是不能达 到目的的,
     它只能从最内层循环退出到上一级的循环。下面是使用 goto 语句的一个例子:

     for (...) {
        for (...) {
            if (disable) {
                goto error;
            }
        }
     }


     error:
     /* clean up the mess */
*/