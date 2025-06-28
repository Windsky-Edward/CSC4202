public class Station {
    public String name;
    public int bikes;
    public int emptySlots;
    public double distanceFromUser;
    public int x, y; //新增：坐标属性

    public Station(String name, int bikes, int emptySlots, double distanceFromUser, int x, int y) {
        this.name = name;
        this.bikes = bikes;
        this.emptySlots = emptySlots;
        this.distanceFromUser = distanceFromUser;
        this.x = x;
        this.y = y;
    }
}
