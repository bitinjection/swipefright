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

  def decode(request) do
    filename = Ecto.UUID.generate <> "." <> Map.get(request, "format")
    base64 = Map.get(request, "content")
    %{:filename => filename, :content => Base.decode64!(base64)}
  end

  def write(file) do
    {:ok, filename} = Map.fetch(file, :filename)
    {:ok, content} = Map.fetch(file, :content)

    tempfile = filename

    {:ok, outfile} = File.open tempfile, [:write]
    IO.binwrite outfile, content
    File.close outfile
  end

  def create(conn, %{"pending_post" => pending_post_params}) do
    pending_post_params
      |> Map.fetch("content")
      |> parse
      |> decode
      |> write

    with {:ok, %PendingPost{} = pending_post} <- Content.create_pending_post(pending_post_params) do
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
