#include <iostream>

using namespace std;

class Horse {
    public:
        Horse() {
           cout << "Horse constructor..." << endl;
        }

        virtual ~Horse() {
           cout << "Horse destructor..." << endl;
        }

        virtual void whinny() const {
            cout << "Whinny..." << endl;
        }

    private:
        int itsAge;
};

class Bird {
    public:
        Bird() {
            cout << "Bird constructor..." << endl;
        }

        virtual ~Bird() {
            cout << "Bird destructor..." << endl;
        }

        virtual void chirp() const {
            cout << "Chirp..." << endl;
        }

        virtual void fly() const {
            cout << "I can fly..." << endl;
        }

    private:
        int itsWeight;
};

// 派生类，多重继承
class Pegasus : public Horse, public Bird {

    public:
        void chirp() const {
            whinny();
        }

        Pegasus() {
            cout << "Pegasus constructor..." << endl;
        }

        ~Pegasus() {
            cout << "Pegasus destructor..." << endl;
        }
};

// 抽象类
class Shape {

    public:
        Shape() {}
        ~Shape() {}

        virtual double getArea() = 0;
        virtual double getPerim() = 0;
        virtual void draw() = 0;
};

void Shape :: draw() {
        cout << "Abstract draw!" << endl;
}

class Circle : public Shape {
    public:
        Circle(int radius) : itsRadius(radius) {}
        virtual ~Circle() {}

        double getArea() {
            return 3.14 * itsRadius * itsRadius;
        }

        double getPerim() {
            return 2 * 3.14 * itsRadius;
        }

        void draw();

    private:
        int itsRadius;
}

void Circle :: draw() {
    cout << "Circle draw!" << endl;
    // 调用父类的函数
    Shape :: draw();
}