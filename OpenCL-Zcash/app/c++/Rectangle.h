class Point {
    public:
        // 内联函数
        void setX(int x) {
            itsX = x;
        }

        void setY(int y) {
            itsY = y;
        }

        int getX() const {
            return itsX;
        }

        int getY() const {
            return itsY;
        }

    private:
        int itsX;
        int itsY;
};

class Rectangle {
    public:
        Rectangle(int top, int left, int bottom, int right);
        ~Rectangle() {}

        int getTop() const {
            return itsTop;
        }

        int getLeft() const {
            return itsLeft;
        }

        int getBottom() const {
            return itsBottom;
        }

        int getRight() const {
            return itsRight;
        }

        void setTop(int top) {
            itsTop = top;
        }

        void setLeft(int left) {
            itsLeft = left;
        }

        void setBottom(int bottom) {
            itsBottom = bottom;
        }

        void setRight(int right) {
            itsRight = right;
        }

        Point getUpperLeft() const {
            return itsUpperLeft;
        }

        Point getLowerLeft() const {
            return itsLowerLeft;
        }

        Point getUpperRight() const {
            return itsUpperRight;
        }

        Point getLowerRight() const {
            return itsLowerRight;
        }

        void setUpperLeft(Point location) {
            itsUpperLeft = location;
        }

        void setLowerLeft(Point location) {
            itsLowerLeft = location;
        }

        void setUpperRight(Point location) {
            itsUpperRight = location;
        }

        void setLowerRight(Point location) {
            itsLowerRight = location;
        }

        int getArea() const;


    private:
        Point itsUpperLeft;
        Point itsUpperRight;
        Point itsLowerLeft;
        Point itsLowerRight;

        int itsLeft;
        int itsRight;
        int itsTop;
        int itsBottom;
};