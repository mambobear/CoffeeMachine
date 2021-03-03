package machine;

import java.util.*;

public class CoffeeMachine {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        CoffeeMaker machine = new CoffeeMaker(550, 400, 540, 120, 9);
        machine.start();

        while (true) {
            String action = scanner.next();
            if ("exit".equals(action)) {
                return;
            }
            machine.performAction(action);
        }
    }
}

class CoffeeMaker {

    private final HashMap<String, State> actionMenu;
    private final HashMap<String, Drink> coffeeMenu;
    private final HashMap<State, String> statePrompts;

    private int amount;
    private int water;
    private int milk;
    private int beans;
    private int cups;

    private State state;
    private String prompt;

    public CoffeeMaker(int amount, int water, int milk, int beans, int cups) {
        this.amount = amount;
        this.water = water;
        this.milk = milk;
        this.beans = beans;
        this.cups = cups;

        state = State.CHOOSING_ACTION;
        prompt = "\nWrite action (buy, fill, take, remaining, exit):";

        actionMenu = initActionMenu();
        coffeeMenu = initCoffeeMenu();
        statePrompts = initStatePrompts();
    }

    private HashMap<State, String> initStatePrompts() {
        HashMap<State, String> prompts = new HashMap<>();
        prompts.put(State.CHOOSING_ACTION, "\nWrite action (buy, fill, take, remaining, exit):");
        prompts.put(State.BUYING_COFFEE, "\nWhat do you want to buy? 1 - espresso, 2 - latte, 3 - cappuccino, back - to main menu:");
        prompts.put(State.FILLING_WATER, "Write how many ml of water do you want to add:");
        prompts.put(State.FILLING_MILK, "Write how many ml of milk do you want to add:");
        prompts.put(State.FILLING_BEANS, "Write how many grams of coffee beans do you want to add:");
        prompts.put(State.FILLING_CUPS, "Write how many disposable cups of coffee do you want to add:");
        return  prompts;
    }

    public void start() {
        System.out.println(prompt);
    }

    public void performAction(String input) {

        switch (state) {
            case CHOOSING_ACTION:
                choose_action(input);
                break;
            case BUYING_COFFEE:
                buy_coffee(input);
                break;
            case FILLING_WATER:
                fillWater(input);
                break;
            case FILLING_MILK:
                fillMilk(input);
                break;
            case FILLING_BEANS:
                fillBeans(input);
                break;
            case FILLING_CUPS:
                fillCups(input);
                break;
        }
        System.out.println(prompt);
    }

    private void choose_action(String action) {
        if (!actionMenu.containsKey(action)) {
            System.out.println("Invalid action");
        }
        switch(actionMenu.get(action)) {
            case BUYING_COFFEE:
                setState(State.BUYING_COFFEE);
                break;
            case FILLING_WATER:
                setState(State.FILLING_WATER);
                break;
            case DISBURSING:
                take();
                break;
            case LOGGING:
                remaining();
                break;
            default:
        }
    }

    private void setState(State state) {
        prompt = statePrompts.get(state);
        this.state = state;
    }

    private void buy_coffee(String input) {
        if ("back".equals(input)) {
            setState(State.CHOOSING_ACTION);
            return;
        }

        if (!coffeeMenu.containsKey(input)) {
            System.out.println("Invalid input");
            return;
        }

        setState(State.CHOOSING_ACTION);
        Drink drink = coffeeMenu.get(input);
        if (canBuy(drink)) {
            System.out.println("I have enough resources, making you a coffee!");
            water -= drink.water;
            milk -= drink.milk;
            beans -= drink.beans;
            cups -= 1;
            amount += drink.price;
        }
    }

    private boolean canBuy(Drink drink) {
        if (drink.water > water) {
            System.out.println("Sorry, not enough water!");
            return false;
        }

        if (drink.milk > milk) {
            System.out.println("Sorry, not enough milk!");
            return false;
        }

        if (drink.beans > beans) {
            System.out.println("Sorry, not enough beans!");
            return false;
        }

        if (cups == 0) {
            System.out.println("Sorry, not enough cup!");
            return false;
        }

        return true;
    }

    private void fillCups(String input) {
        int quantity;
        try {
            quantity = getQuantity(input);
        } catch (IncorrectQuantityException e) {
            System.out.println("Invalid Input");
            return;
        }

        this.cups += quantity;
        setState(State.CHOOSING_ACTION);
    }

    private void fillBeans(String input) {
        int quantity;
        try {
            quantity = getQuantity(input);
        } catch (IncorrectQuantityException e) {
            System.out.println("Invalid Input");
            return;
        }

        this.beans += quantity;
        setState(State.FILLING_CUPS);
    }

    private void fillMilk(String input) {
        int quantity;
        try {
            quantity = getQuantity(input);
        } catch (IncorrectQuantityException e) {
            System.out.println("Invalid Input");
            return;
        }

        this.milk += quantity;
        setState(State.FILLING_BEANS);
    }

    private void fillWater(String input) {
        int quantity;
        try {
            quantity = getQuantity(input);
        } catch (IncorrectQuantityException e) {
            System.out.println("Invalid Input");
            return;
        }

        this.water += quantity;
        setState(State.FILLING_MILK);
    }

    private int getQuantity(String input) throws IncorrectQuantityException {
        int quantity;
        try {
            quantity = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new IncorrectQuantityException("Invalid quantity");
        }
        if (quantity < 0) {
            throw new IncorrectQuantityException("Invalid quantity");
        }
        return quantity;
    }

    private void take() {
        System.out.printf("\nI gave you $%d", amount);
        amount = 0;
    }

    private void remaining() {
        System.out.println();
        System.out.println(this);
    }

    public String toString() {
        return "The coffee machine has:\n" + String.format("%d of water\n", water) +
                String.format("%d of milk\n", milk) +
                String.format("%d of coffee beans\n", beans) +
                String.format("%d of disposable cups\n", cups) +
                String.format("$%d of money", amount);
    }

    private HashMap<String, State> initActionMenu() {
        HashMap<String, State> menuItems = new HashMap<>();
        menuItems.put("buy", State.BUYING_COFFEE);
        menuItems.put("fill", State.FILLING_WATER);
        menuItems.put("take", State.DISBURSING);
        menuItems.put("remaining", State.LOGGING);
        return  menuItems;
    }

    private HashMap<String, Drink> initCoffeeMenu() {
        HashMap<String, Drink> menuItems = new HashMap<>();
        menuItems.put("1", new Drink(250, 0, 16, 4));
        menuItems.put("2", new Drink(350, 75, 20, 7));
        menuItems.put("3", new Drink(200, 100, 12, 6));
        return  menuItems;
    }

    private static class Drink {
        private final int water;
        private final int milk;
        private final int beans;
        private final int price;

        Drink(int water, int milk, int beans, int price) {
            this.water= water;
            this.milk = milk;
            this.beans = beans;
            this.price = price;

        }
    }

    private enum State {
        CHOOSING_ACTION, BUYING_COFFEE,
        FILLING_WATER,  FILLING_MILK, FILLING_BEANS, FILLING_CUPS,
        DISBURSING, LOGGING
    }
}

class IncorrectQuantityException extends Exception {
    public IncorrectQuantityException(String errorMessage) {
        super(errorMessage);
    }
}