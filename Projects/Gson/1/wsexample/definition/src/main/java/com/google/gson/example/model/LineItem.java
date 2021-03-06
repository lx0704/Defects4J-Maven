/*
 * Copyright (C) 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.gson.example.model;

/**
 * A line item in a cart
 *
 * @author inder
 */
public class LineItem {
  private final String name;
  private final int quantity;
  private final long priceInMicros;
  private final String currencyCode;

  public LineItem(String name, int quantity, long priceInMicros, String currencyCode) {
    this.name = name;
    this.quantity = quantity;
    this.priceInMicros = priceInMicros;
    this.currencyCode = currencyCode;
  }

  public String getName() {
    return name;
  }

  public int getQuantity() {
    return quantity;
  }

  public long getPriceInMicros() {
    return priceInMicros;
  }

  public String getCurrencyCode() {
    return currencyCode;
  }
}
