package dev.tr1ngle.chatchannels;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

public class ListArgument<T, A extends ArgumentType<T>> implements ArgumentType<List<T>>
{
	private final A arg;

	private ListArgument(A arg)
	{
		this.arg = arg;
	}

	@Override
	public List<T> parse(StringReader reader) throws CommandSyntaxException
	{
		List<T> result = new ArrayList<>();
		int cursor = reader.getCursor();

		try
		{
			while (reader.canRead())
			{
				cursor = reader.getCursor();
				result.add(arg.parse(reader));

				if (reader.canRead())
				{
					reader.expect(' ');
				}
			}
		}
		catch (CommandSyntaxException e)
		{
			if (result.isEmpty())
			{
				throw e;
			}
			reader.setCursor(cursor);
		}

		if (result.isEmpty())
		{
			throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().create();
		}

		return result;
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> ctx, SuggestionsBuilder builder)
	{
		StringReader reader = new StringReader(builder.getInput());
		reader.setCursor(builder.getStart());

		int cursor = reader.getCursor();

		try
		{
			while (reader.canRead())
			{
				arg.parse(reader);

				if (reader.canRead())
				{
					reader.expect(' ');
					cursor = reader.getCursor();
				}
			}
		}
		catch (CommandSyntaxException e)
		{
		}

		return arg.listSuggestions(ctx, builder.createOffset(cursor));
	}

	@Override
	public Collection<String> getExamples()
	{
		return arg.getExamples();
	}

	public static <T, A extends ArgumentType<T>> ListArgument<T, A> of(A arg)
	{
		return new ListArgument<>(arg);
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> getList(final CommandContext<?> ctx, final String name)
	{
		return (List<T>)ctx.getArgument(name, List.class);
	}
}
