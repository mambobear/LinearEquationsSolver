package solver;

public class Complex {

    final private double real;
    final private double imaginary;

    Complex(double real, double imaginary) {
        this.real = real;
        this.imaginary = imaginary;
    }

    public double getReal() {
        return real;
    }

    public double getImaginary() {
        return imaginary;
    }

    public Complex add(Complex z) {
        double real = this.real + z.getReal();
        double imaginary = this.imaginary + z.getImaginary();
        return new Complex(real, imaginary);
    }

    public Complex subtract(Complex z) {
        double real = this.real - z.getReal();
        double imaginary = this.imaginary - z.getImaginary();
        return new Complex(real, imaginary);
    }

    public Complex multiply(Complex z) {
        double real = this.real * z.getReal() - this.imaginary * z.getImaginary();
        double imaginary = this.real * z.getImaginary() + this.imaginary * z.getReal();
        return new Complex(real, imaginary);
    }

    public Complex multiply(double r) {
        double real = this.real * r;
        double imaginary = this.imaginary * r;
        return new Complex(real, imaginary);
    }

    public Complex divideBy(Complex z) {
        double modSquare = z.real * z.real + z.imaginary * z.imaginary;
        return this.multiply(z.conjugate()).multiply(new Complex(1.0 / modSquare, 0));
    }

    public Complex conjugate() {
        return new Complex(this.real, -this.imaginary);
    }

    public String toString() {
        if (this.real == 0 && this.imaginary == 0) return "0";
        else if (this.imaginary == 0) return String.format("%f", this.real);
        else if (this.real == 0) {
            if (this.imaginary == 1) return "i";
            else if (this.imaginary == -1) return "-i";
            else return String.format("%fi", this.imaginary);
        } else {
            if (this.imaginary == 1) {
                return String.format("%f+i", this.real );
            } else if (this.imaginary == -1) {
                return String.format("%f-i", this.real );
            } else if (this.imaginary > 0) {
                return String.format("%f+%fi", this.real, this.imaginary);
            } else {
                return String.format("%f%fi", this.real, this.imaginary);
            }
        }
    }

    public boolean equal(double r) {
        return this.real == r && this.imaginary == 0;
    }
    public boolean equal(Complex z) {
        return this.real == z.getReal() && this.imaginary == z.getImaginary();
    }

}
