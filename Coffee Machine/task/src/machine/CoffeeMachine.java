package machine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import java.util.Scanner;
public class CoffeeMachine {
    public static void main(String[] args) {

        // stage1();

        // stage2();

        stage3();
    }

    private static void stage3() {
        Scanner scanner = new Scanner(System.in);

        CoffeeMaker machine = new CoffeeMaker();
        machine.start();

        while (true) {
            String action = scanner.next();
            if ("exit".equals(action)) {
                return;
            }
            machine.performAction(action);
        }
    }

    private static void stage2() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Write how many ml of water the coffee machine has:");
        int water = scanner.nextInt();

        System.out.println("Write how many ml of milk the coffee machine has:");
        int milk = scanner.nextInt();

        System.out.println("Write how many grams of coffee beans the coffee machine has:");
        int beans = scanner.nextInt();

        System.out.println("Write how many cups of coffee you will need:");
        int cupsOfCoffee = scanner.nextInt();

        int nMaxNumberOfCups =  Collections.min(Arrays.asList(water / 200, milk / 50, beans / 15));
        if(nMaxNumberOfCups < cupsOfCoffee) {
            System.out.printf("No, I can make only %d cup(s) of coffee\n", nMaxNumberOfCups);
        } else {
            String str = "Yes, I can make that amount of coffee";
            if (nMaxNumberOfCups > cupsOfCoffee) {
                str = str + " (and even "+ (nMaxNumberOfCups - cupsOfCoffee) + " more than that)";
            }
            System.out.println(str);
        }
    }

    private static void stage1() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Write how many cups of coffee you will need:");
        int nCups = scanner.nextInt();

        int milk = nCups * 50;
        int beans = nCups * 15;
        int water = nCups * 200;
        System.out.printf("For %d cups of coffee you will need:\n" +
                "%d ml of water\n" +
                "%d ml of milk\n" +
                "%d g of coffee beans", nCups, water, milk, beans);
    }
}

class CoffeeMaker {

    private final ArrayList<String> actionMenu = new ArrayList<>(Arrays.asList("buy", "fill", "take", "remaining", "exit"));
    private final ArrayList<String> coffeeMenu = new ArrayList<>(Arrays.asList("1", "2", "3", "back"));

    private int amount = 550;
    private int water = 400;
    private int milk = 540;
    private int beans = 120;
    private int cups = 9;

    private State state = State.CHOOSING_ACTION;
    private String prompt = "\nWrite action (buy, fill, take, remaining, exit):";

    public void start() {
        System.out.println(prompt);
    }

    private enum State {
        CHOOSING_ACTION, BUYING_COFFEE,
        FILLING_WATER,  FILLING_MILK, FILLING_BEANS, FILLING_CUPS,
    }

    final Drink[] drinks = new Drink[] {
            new Drink(250, 0, 16, 4),   // espresso
            new Drink(350, 75, 20, 7),  // latte
            new Drink(200, 100, 12, 6)  // cappuccino
    };

    public String toString() {
        return "The coffee machine has:\n" + String.format("%d of water\n", water) +
                String.format("%d of milk\n", milk) +
                String.format("%d of coffee beans\n", beans) +
                String.format("%d of disposable cups\n", cups) +
                String.format("$%d of money", amount);
    }

    private void setState(State state) {
        switch (state) {
            case CHOOSING_ACTION:
                prompt = "\nWrite action (buy, fill, take, remaining, exit):";
                break;
            case BUYING_COFFEE:
                prompt = "\nWhat do you want to buy? 1 - espresso, 2 - latte, 3 - cappuccino, back - to main menu:";
                break;
            case FILLING_WATER:
                prompt = "Write how many ml of water do you want to add:";
                break;
            case FILLING_MILK:
                prompt = "Write how many ml of milk do you want to add:";
                break;
            case FILLING_BEANS:
                prompt = "Write how many grams of coffee beans do you want to add:";
                break;
            case FILLING_CUPS:
                prompt = "Write how many disposable cups of coffee do you want to add:";
                break;
        }
        this.state = state;
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

    private void buy_coffee(String input) {
        int choice = coffeeMenu.indexOf(input);

        if (choice < 0 || choice > 3) {
            System.out.println("Invalid input");
            return;
        }

        setState(State.CHOOSING_ACTION);

        if (choice == 3) {
            return;
        }

        Drink drink = drinks[choice];
        if (canBuy(drink)) {
            System.out.println("I have enough resources, making you a coffee!");
            water -= drink.water;
            milk -= drink.milk;
            beans -= drink.beans;
            cups -= 1;
            amount += drink.price;
        }
    }

    private void choose_action(String action) {
        switch(actionMenu.indexOf(action)) {
            case 0:
                setState(State.BUYING_COFFEE);
                break;
            case 1:
                setState(State.FILLING_WATER);
                break;
            case 2:
                take();
                break;
            case 3:
                remaining();
                break;
            default:
                System.out.println("Invalid action");
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

    private void take() {
        System.out.printf("\nI gave you $%d", amount);
        amount = 0;
    }

    private void remaining() {
        System.out.println();
        System.out.println(this);
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
}

class IncorrectQuantityException extends Exception {
    public IncorrectQuantityException(String errorMessage) {
        super(errorMessage);
    }
}
