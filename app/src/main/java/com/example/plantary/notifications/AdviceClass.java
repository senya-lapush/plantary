package com.example.plantary.notifications;

import java.util.Random;

public final class AdviceClass {
    private static final String[] advice = {
            "Располагайте растения ближе к свету.",
            "Поливайте растения отстоявшейся водой комнатной температуры.",
            "Рыхляйте поверхность почвы в коршочке, чтобы к корням поступал воздух.",
            "Вытирайте пыль с листьев влажной тряпочкой.",
            "Опрыскивайте растения водой комнатной температуры.",
            "Срезайте с растений сухие листья и веточки.",
            "Следите за чистотой цветочных горшков и подставок.",
            "Регулярно подкармливайте растения удобрениями"
    };

    public static String getAdvice() {
        Random random = new Random();
        int size = advice.length;

        return advice[random.nextInt(size)];
    }
}
