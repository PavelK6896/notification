package ru.geekbrains.utils;

import ru.geekbrains.entity.Ad;

import java.util.List;
import java.util.Map;

public class Formatter {
    public static String adsToString (Map<String, List<Ad>> maps) {
        StringBuilder stringBuilder = new StringBuilder();
        maps.forEach((key, adsList) -> {
            stringBuilder.append(" --- " + "\n");
            stringBuilder.append("*" + adsList.get(0).getTitle() + "*\n");
//            stringBuilder.append(a.getDescription() + "\n");
            stringBuilder.append("Цена: " + adsList.get(0).getPrice() + "\n");
            stringBuilder.append("Комнат: " + adsList.get(0).getRooms() + "\n");
            stringBuilder.append("Квадратура: " + adsList.get(0).getArea() + "\n");
            stringBuilder.append("Этаж: " + adsList.get(0).getFloor() + "\n");
            stringBuilder.append("Адрес: " + adsList.get(0).getAddress().getAddressToString() + "\n");
            stringBuilder.append("Ссылка: " + adsList.get(0).getLink() + "\n");

            if(adsList.size() > 1) {
                // если есть дубликаты
                stringBuilder.append("      дубликаты ↓ " + "\n");
                for (int i = 1; i < adsList.size(); i++) {
                    stringBuilder.append("      . . . \n");
//                    stringBuilder.append("      " + " --- " + "\n");
                    stringBuilder.append("      " + adsList.get(i).getTitle() + "\n");
//            stringBuilder.append(a.getDescription() + "\n");
                    stringBuilder.append("      " + "Цена: " + adsList.get(i).getPrice() + "\n");
//                    stringBuilder.append("      " + "Комнат: " + adsList.get(i).getRooms() + "\n");
//                    stringBuilder.append("      " + "Квадратура: " + adsList.get(i).getArea() + "\n");
//                    stringBuilder.append("      " + "Адрес: " + adsList.get(i).getAddress().getAddressToString() + "\n");
                    stringBuilder.append("      " + "Ссылка: " + adsList.get(i).getLink() + "\n");

                }
            }
        });

        return stringBuilder.toString();
    }
}
