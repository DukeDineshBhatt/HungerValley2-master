package my.dinesh.hungervalley;

public class ReviewDataSetGet {

    String  review;
    double rating;


    public ReviewDataSetGet() {

    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public ReviewDataSetGet(String review, double rating) {
        this.review = review;
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }
}
