#include <iostream>

using namespace std;

enum BREED {GOLDEN, CAIRN, DANDIE, SHETLAND, DOBERMAN, LAB};

class Mammal {
    public:
        Mammal() : itsAge(2), itsWeight(5){}
        virtual ~Mammal(){}

        int getAge() const {
            return itsAge;
        }
        void setAge(int age) {
            itsAge = age;
        }
        int getWeight() const {
            return itsWeight;
        }
        void setWeight(int weight) {
            itsWeight = weight;
        }

        virtual void speak() const {
            cout << "Mammal sound!" << endl;
        }
        void sleep() const {
            cout << "Mammal sleep!" << endl;
        }


    protected:
        int itsAge;
        int itsWeight;
};

class Dog : public Mammal {
    public:
        Dog() : itsBreed(GOLDEN) {}
        virtual ~Dog() {}

        BREED getBreed() const {
            return itsBreed;
        }

        void setBreed(BREED breed) {
            itsBreed = breed;
        }

        void wagTail() {
            cout << "Tail wagging..." << endl;
        }

        void begForFood() {
            cout << "Begging for food..." << endl;
        }

        void speak() const{
            Mammal :: speak();
            cout << "Dog is speeking..." << endl;
        }

    protected:
        BREED itsBreed;
};

class ElectriMotor {

    public:
        ElectriMotor() {}
        virtual ~ElectriMotor() {}

        void startMotor() {
            accelerate();
            cruise();
        }

        void stopMotor() {
            cout << "Motor stopped!" << endl;
        }

    private:
        void accelerate() {
            cout << "Motor started!" << endl;
        }

        void cruise() {
            cout << "Motor running at const speed!" << endl;
        }
};

// 私有继承
// class Fan : private ElectriMotor {
    //public:
      // Fan() {}
      // ~Fan() {}

      // void startFan() {
      //     startMotor();
      // }

      // void stopFan() {
      //     stopMotor();
       //}
//};


// 聚合或组合
class Fan {
    public:
        Fan() {}
        ~Fan() {}

        void startFan() {
            mMotor.startMotor();
        }

        void stopFan() {
            mMotor.stopMotor();
        }

    private:
        ElectriMotor mMotor;
};