class Dog {
    public:
        // 构造函数
        Dog();
        Dog (unsigned int age, unsigned int weight);
        // 析构函数
        ~Dog();
        // public accessors
        inline unsigned int getAge() const;
        // 不可以声明为const
        void setAge(unsigned int age);

        unsigned int getWeight() const;
        // 不可以声明为const
        void setWeight(unsigned int weight);

        // public member function
        void meow();


    private:
        unsigned int age;
        unsigned int weight;
};