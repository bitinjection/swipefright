defmodule ApiWeb.PendingPostController do
  use ApiWeb, :controller

  alias Api.Content
  alias Api.Content.PendingPost

  action_fallback ApiWeb.FallbackController

  def index(conn, _params) do
    pending = Content.list_pending()
    render(conn, "index.json", pending: pending)
  end

  def parse({:ok, s}) do
    Regex.named_captures(~r/data:image\/(?<format>.*);.*,(?<content>.*)/,s)
  end

  def decode(%{"format" => format, "content" => content}, base_filename) do
    filename = base_filename <> "." <> format
    %{:filename => filename, :content => Base.decode64!(content)}
  end

  def write(file, path) do
    {:ok, filename} = Map.fetch(file, :filename)
    {:ok, content} = Map.fetch(file, :content)
    full_filename = path <> filename

    {:ok, outfile} = File.open full_filename, [:write]
    IO.binwrite outfile, content
    File.close outfile
    filename
  end

  def save_file(path) do
  end

  def create(conn, %{"pending_post" => pending_post_params}) do
    base_filename = Ecto.UUID.generate 

    written_filename = pending_post_params
      |> Map.fetch("image")
      |> parse
      |> decode(base_filename)
      |> write("/images/pending/")

    {_, formatted_post} = Map.get_and_update(pending_post_params,
                                        "image", fn current -> {current, written_filename} end)

    with {:ok, %PendingPost{} = pending_post} <- Content.create_pending_post(formatted_post) do
      conn
      |> put_status(:created)
      |> put_resp_header("location", pending_post_path(conn, :show, pending_post))
      |> render("show.json", pending_post: pending_post)
    end
  end

  def show(conn, %{"id" => id}) do
    pending_post = Content.get_pending_post!(id)
    render(conn, "show.json", pending_post: pending_post)
  end

  def update(conn, %{"id" => id, "pending_post" => pending_post_params}) do
    pending_post = Content.get_pending_post!(id)

    with {:ok, %PendingPost{} = pending_post} <- Content.update_pending_post(pending_post, pending_post_params) do
      render(conn, "show.json", pending_post: pending_post)
    end
  end

  def delete(conn, %{"id" => id}) do
    pending_post = Content.get_pending_post!(id)
    with {:ok, %PendingPost{}} <- Content.delete_pending_post(pending_post) do
      send_resp(conn, :no_content, "")
    end
  end
end
