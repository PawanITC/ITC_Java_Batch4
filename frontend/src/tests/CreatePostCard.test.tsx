import { act, fireEvent, render, screen, waitFor } from "@testing-library/react";
import CreatePostCard from "../components/feed/CreatePostCard";

describe("CreatePostCard", () => {
  test("renders composer actions and starts with the submit button disabled", () => {
    render(<CreatePostCard onCreate={jest.fn()} />);

    expect(screen.getByPlaceholderText(/start a post/i)).toBeInTheDocument();
    expect(screen.getByRole("button", { name: /photo/i })).toBeInTheDocument();
    expect(screen.getByRole("button", { name: /video/i })).toBeInTheDocument();
    expect(screen.getByRole("button", { name: /event/i })).toBeInTheDocument();
    expect(screen.getByRole("button", { name: /article/i })).toBeInTheDocument();
    expect(screen.getByRole("button", { name: /post/i })).toBeDisabled();
  });

  test("expands the composer when it receives focus", () => {
    render(<CreatePostCard onCreate={jest.fn()} />);

    const textarea = screen.getByPlaceholderText(/start a post/i);
    expect(textarea).toHaveAttribute("rows", "1");

    fireEvent.focus(textarea);

    expect(textarea).toHaveAttribute("rows", "3");
  });

  test("enables submit only when content has non-whitespace text", () => {
    render(<CreatePostCard onCreate={jest.fn()} />);

    const textarea = screen.getByPlaceholderText(/start a post/i);
    const postButton = screen.getByRole("button", { name: /post/i });

    fireEvent.change(textarea, { target: { value: "   " } });
    expect(postButton).toBeDisabled();

    fireEvent.change(textarea, { target: { value: "Hello network" } });
    expect(postButton).toBeEnabled();
  });

  test("submits trimmed post content and resets the composer on success", async () => {
    const onCreate = jest.fn().mockResolvedValue(undefined);
    render(<CreatePostCard onCreate={onCreate} />);

    const textarea = screen.getByPlaceholderText(/start a post/i);
    fireEvent.change(textarea, { target: { value: "  Hello network  " } });
    fireEvent.click(screen.getByRole("button", { name: /post/i }));

    await waitFor(() => expect(textarea).toHaveValue(""));
    expect(onCreate).toHaveBeenCalledWith("Hello network");
    expect(screen.getByRole("button", { name: /post/i })).toBeDisabled();
  });

  test("does not submit blank content", () => {
    const onCreate = jest.fn();
    render(<CreatePostCard onCreate={onCreate} />);

    fireEvent.change(screen.getByPlaceholderText(/start a post/i), {
      target: { value: "   " },
    });
    fireEvent.click(screen.getByRole("button", { name: /post/i }));

    expect(onCreate).not.toHaveBeenCalled();
    expect(screen.getByRole("button", { name: /post/i })).toBeDisabled();
  });

  test("prevents duplicate submissions while a post is being created", async () => {
    let resolveCreate: () => void = () => undefined;
    const onCreate = jest.fn(
      () =>
        new Promise<void>((resolve) => {
          resolveCreate = resolve;
        })
    );
    render(<CreatePostCard onCreate={onCreate} />);

    fireEvent.change(screen.getByPlaceholderText(/start a post/i), {
      target: { value: "One careful post" },
    });

    fireEvent.click(screen.getByRole("button", { name: /post/i }));

    await waitFor(() =>
      expect(screen.getByRole("button", { name: /posting/i })).toBeDisabled()
    );
    fireEvent.click(screen.getByRole("button", { name: /posting/i }));

    expect(onCreate).toHaveBeenCalledTimes(1);
    await act(async () => {
      resolveCreate();
    });
  });

  test("shows an error and keeps content when post creation fails", async () => {
    const consoleError = jest
      .spyOn(console, "error")
      .mockImplementation(() => undefined);
    const onCreate = jest.fn().mockRejectedValue(new Error("Network error"));
    render(<CreatePostCard onCreate={onCreate} />);

    const textarea = screen.getByPlaceholderText(/start a post/i);
    fireEvent.change(textarea, { target: { value: "Please try this later" } });
    fireEvent.click(screen.getByRole("button", { name: /post/i }));

    expect(
      await screen.findByText(/post could not be shared\. please try again\./i)
    ).toBeInTheDocument();
    expect(textarea).toHaveValue("Please try this later");
    expect(screen.getByRole("button", { name: /post/i })).toBeEnabled();

    consoleError.mockRestore();
  });
});
