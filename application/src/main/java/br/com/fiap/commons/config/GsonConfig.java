package br.com.fiap.commons.config;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Configuration
public class GsonConfig {

	@Bean
	public Gson gson() {
		return new GsonBuilder()
				.setPrettyPrinting()
				.registerTypeAdapterFactory(new EmptyListToNullFactory())
				.registerTypeAdapter(LocalDate.class, new LocalDateAdapter().serialize())
				.registerTypeAdapter(LocalDate.class, new LocalDateAdapter().deserialize())
				.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter().serialize())
				.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter().deserialize())
				.registerTypeAdapter(YearMonth.class, new YearMonthAdapter().serialize())
				.registerTypeAdapter(YearMonth.class, new YearMonthAdapter().deserialize())
				.registerTypeAdapter(Year.class, new YearAdapter().serialize())
				.registerTypeAdapter(Year.class, new YearAdapter().deserialize())
				.create();
	}

	interface GsonRegistrable<T> {

		JsonSerializer<T> serialize();

		JsonDeserializer<T> deserialize();
	}

	@NoArgsConstructor(access = PRIVATE)
	static class LocalDateAdapter implements GsonRegistrable<LocalDate> {
		private static final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		@Override
		public JsonSerializer<LocalDate> serialize() {
			return (src, typeOfSrc, context) -> new JsonPrimitive(src.format(df));
		}

		@Override
		public JsonDeserializer<LocalDate> deserialize() {
			return (json, type, context) -> LocalDate.parse(json.getAsJsonPrimitive().getAsString(), df);
		}
	}

	@NoArgsConstructor(access = PRIVATE)
	static class LocalDateTimeAdapter implements GsonRegistrable<LocalDateTime> {
		private static final DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

		private static final DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss" +
				"[.SSSSSSSSS]" +
				"[.SSSSSSSS]" +
				"[.SSSSSSS]" +
				"[.SSSSSS]" +
				"[.SSSSS]" +
				"[.SSSS]" +
				"[.SSS]");

		@Override
		public JsonSerializer<LocalDateTime> serialize() {
			return (src, typeOfSrc, context) -> new JsonPrimitive(src.format(dtf1));
		}

		@Override
		public JsonDeserializer<LocalDateTime> deserialize() {
			return (json, type, context) -> LocalDateTime.parse(json.getAsJsonPrimitive().getAsString(), dtf2);
		}
	}

	@NoArgsConstructor(access = PRIVATE)
	static class YearMonthAdapter implements GsonRegistrable<YearMonth> {
		private static final DateTimeFormatter ymf = DateTimeFormatter.ofPattern("yyyy-MM");

		@Override
		public JsonSerializer<YearMonth> serialize() {
			return (src, typeOfSrc, context) -> new JsonPrimitive(src.format(ymf));
		}

		@Override
		public JsonDeserializer<YearMonth> deserialize() {
			return (json, type, context) -> YearMonth.parse(json.getAsJsonPrimitive().getAsString(), ymf);
		}
	}

	@NoArgsConstructor(access = PRIVATE)
	static class YearAdapter implements GsonRegistrable<Year> {
		private static final DateTimeFormatter yf = DateTimeFormatter.ofPattern("yyyy");

		@Override
		public JsonSerializer<Year> serialize() {
			return (src, typeOfSrc, context) -> new JsonPrimitive(src.format(yf));
		}

		@Override
		public JsonDeserializer<Year> deserialize() {
			return (json, type, context) -> Year.parse(json.getAsJsonPrimitive().getAsString(), yf);
		}
	}

	private static class EmptyListToNullFactory implements TypeAdapterFactory {

		@Override
		public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
			var rawType = type.getRawType();
			if (!List.class.isAssignableFrom(rawType))
				return null;

			@SuppressWarnings("unchecked")
			var delegate = (TypeAdapter<List<Object>>) (gson.getDelegateAdapter(this, type));

			@SuppressWarnings("unchecked")
			var adapter = (TypeAdapter<T>) new TypeAdapter<List<Object>>() {
				@Override
				public List<Object> read(JsonReader in) throws IOException {
					return delegate.read(in);
				}

				@Override
				public void write(JsonWriter out, List<Object> value) throws IOException {
					if (value == null || value.isEmpty())
						delegate.write(out, null);
					else
						delegate.write(out, value);
				}
			};
			return adapter;
		}
	}
}
